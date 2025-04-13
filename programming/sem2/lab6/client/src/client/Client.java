package client;

import client.input_managers.InputManager;
import general.ClientCommand;
import general.Reader;
import client.commands.*;
import general.Request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Client {
    private InetAddress host;
    private int port;
    private boolean initialRequestNeeded = true;
    private static final int MAX_RECONNECT_ATTEMPTS = 10;
    private int connectionAttempts = 0;

    public Client(InetAddress host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() {
        while (connectionAttempts < MAX_RECONNECT_ATTEMPTS) {
            try (SocketChannel client = connectToServer()) {
                initialRequestNeeded = true; // Сбрасываем флаг при новом подключении
                workWithServer(client);
            } catch (IOException | InterruptedException | ClassNotFoundException e) {
                handleDisconnect();
            }
        }
        System.out.println("Maximum connection attempts reached. Exiting.");
        System.exit(0);
    }

    private SocketChannel connectToServer() throws IOException, InterruptedException {
        while (true) {
            try {
                SocketChannel client = SocketChannel.open(new InetSocketAddress(host, port));
                client.configureBlocking(false);
                System.out.println("Connected to server!");
                connectionAttempts = 0; // Сброс счетчика при успешном подключении
                return client;
            } catch (IOException e) {
                handleConnectionError();
            }
        }
    }

    private void workWithServer(SocketChannel client) throws IOException, ClassNotFoundException {
        Selector selector = Selector.open();
        client.register(selector, SelectionKey.OP_WRITE);

        while (client.isConnected()) {
            selector.select(5000); // Таймаут 5 секунд

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isWritable()) {
                    handleWriteOperation(client, key);
                }
                if (key.isReadable()) {
                    handleReadOperation(client, key);
                }
            }

            // Проверка таймаута
            if (initialRequestNeeded && !client.isConnected()) {
                throw new IOException("Connection lost during initial request");
            }
        }
    }

    private void handleWriteOperation(SocketChannel client, SelectionKey key) throws IOException {
        if (initialRequestNeeded) {
            sendInitialRequest(client);
            initialRequestNeeded = false;
        } else {
            ClientCommand command = InputManager.startInput();
            if (command != null) {
                sendRequest(client, command.toRequest());
            }
        }
        key.interestOps(SelectionKey.OP_READ);
    }

    private void handleReadOperation(SocketChannel client, SelectionKey key)
            throws IOException, ClassNotFoundException {

        String answer = (String) Reader.reader(client);
        System.out.println(answer);
        key.interestOps(SelectionKey.OP_WRITE);
    }

    private void sendInitialRequest(SocketChannel channel) throws IOException {
        Request<String, String> request = new PathSetter(null, Main.file).toRequest();
        System.out.println("Sending initial request: " + request.getArg());
        sendRequest(channel, request);
    }

    private void handleDisconnect() {
        System.out.println("Connection lost. Reconnecting...");
        connectionAttempts++;
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void handleConnectionError() throws InterruptedException {
        System.out.println("Connection failed. Attempt " +
                (connectionAttempts + 1) + "/" + MAX_RECONNECT_ATTEMPTS);
        connectionAttempts++;
        if (connectionAttempts >= MAX_RECONNECT_ATTEMPTS) {
            return;
        }
        Thread.sleep(5000);
    }

    private void sendRequest(SocketChannel channel, Request<String, ?> request) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(request);
            byte[] data = baos.toByteArray();

            ByteBuffer sizeBuffer = ByteBuffer.allocate(4).putInt(data.length).flip();
            channel.write(sizeBuffer);

            ByteBuffer dataBuffer = ByteBuffer.wrap(data);
            while (dataBuffer.hasRemaining()) {
                channel.write(dataBuffer);
            }
        }
    }

    public static void main(String[] args) {
        try {
            Client client = new Client(InetAddress.getByName("127.0.0.1"), 8080);
            client.run();
        } catch (IOException e) {
            System.out.println("Error starting client: " + e.getMessage());
        }
    }
}