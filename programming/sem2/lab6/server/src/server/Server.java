package server;

import general.Reader;
import general.Request;
import general.ClientCommand;
import server.commands.Save;
import server.utility.HistoryParser;
import server.utility.MyLogger;
import server.utility.Parser;
import server.utility.RequestRunner;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class Server {
    private SocketChannel client = null;
    private int port;
    private RequestRunner runner = new RequestRunner();
    private boolean flag = true;
    private ServerSocketChannel serverSocketChannel;

    public Server(int port) {
        this.port = port;
    }

    public void run() throws IOException {
        try {
            serverSocketChannel = ServerSocketChannel.open(); // создаем серверный канал (прослушиваем подключения)
            serverSocketChannel = serverSocketChannel.bind(new InetSocketAddress(port)); // прослушиваем порт
            serverSocketChannel.configureBlocking(false); // неблокирующий режим

            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT); // регистрируем сервер, смотрим подключения
            MyLogger.info("Server started on port " + port);
            while (true) {
                int count = selector.select(); // ждем событие, пока в блоке
                if (count == 0) {
                    continue;
                }
                Set<SelectionKey> selectionKeys = selector.selectedKeys(); // хранятся ключи с событиями
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    if (!key.isValid()) {
                        continue; // пропустить невалидные ключи
                    }

                    if (key.isAcceptable()) { // если клиент подключился
                        ServerSocketChannel server = (ServerSocketChannel) key.channel(); // регистрируем канал
                        client = server.accept();
                        client.configureBlocking(false);
                        MyLogger.info("New client connected: " + client.getRemoteAddress());
                        client.register(selector, SelectionKey.OP_READ); // событие возникает, когда в канале что-то появляется
                    }
                    if (key.isReadable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        try {
                            Request<String, ?> request = (Request<String, ?>) Reader.reader(client);
                            if (request == null) {
                                MyLogger.info("Client " + client.getRemoteAddress() + " has disconected");
                                key.cancel();
                                client.close();
                                Save.execute();
                                continue;
                            } else {
                                if (flag) {
                                    runner.run(request);
                                    Parser.fromJson(); // парсим в коллецию
                                    HistoryParser.parseToList(); // парсим историю
                                    flag = false;
                                }
                                String answer = runner.run(request); // обработка запроса
                                MyLogger.info("Server received something");
                                key.attach(answer);
                            }
                            if (key.isValid()) {
                                key.interestOps(SelectionKey.OP_WRITE); // ключ в режим записи
                            }
                        } catch (IOException | ClassNotFoundException e) {
                            MyLogger.info("Error reading from client " + client.getRemoteAddress() + ": " + e.getMessage());
                            key.cancel();
                            key.channel().close();
                        }
                    }

                    if (key.isValid() && key.isWritable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        String answer = (String) key.attachment();
                        if (answer != null) {
                            Response.send(client, answer); // отправляем ответ
                            key.attach(null);
                        }
                        if (key.isValid()) {
                            key.interestOps(SelectionKey.OP_READ); // обратно в режим чтения
                        }
                    }

                    if (!key.channel().isOpen()) {
                        key.cancel();
                    }

                }
            }
        } catch (IOException e) {
            if (client == null) {
                MyLogger.info(e.getMessage());
            } else {
                Response.send(client, "ERROR: " + e.getMessage());
                MyLogger.info("Disconnecting client due to error: " + e.getMessage());
                client.close();
            }
        }
    }
}
