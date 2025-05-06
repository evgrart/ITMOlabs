package client;

import client.commands.Password;
import client.input_managers.InputManager;
import general.ClientCommand;
import general.Request;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

public class Client {
    public static String login;
    private final int port;
    private static final int MAX_RECONNECT_ATTEMPTS = 10;
    private static final int RECONNECT_DELAY_MS = 5000;

    public Client(int port) {
        this.port = port;
    }

    public void run() {
        int connectionAttempts = 0;

        while (connectionAttempts <= MAX_RECONNECT_ATTEMPTS) {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress("localhost", this.port), 5000);
                System.out.println("Connected to " + socket.getRemoteSocketAddress());
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

                workWithServer(socket, output, input);
                return;

            } catch (IOException | ClassNotFoundException e) {
                connectionAttempts++;
                System.out.println("Connection error (" + e.getMessage() + "). Attempt: " + connectionAttempts);

                if (connectionAttempts < MAX_RECONNECT_ATTEMPTS - 1) {
                    try {
                        Thread.sleep(RECONNECT_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        System.out.println("Reconnect interrupted");
                        return;
                    }
                }
            }
        }

        System.out.println("Failed to connect after " + MAX_RECONNECT_ATTEMPTS + " attempts");
        System.exit(1);
    }

    private void workWithServer(Socket socket, ObjectOutputStream output, ObjectInputStream input)
            throws IOException, ClassNotFoundException {
        authenticate(output, input);


        while (!socket.isClosed()) {
            ClientCommand command = InputManager.startInput();
            if (command == null) continue;

            output.writeObject(command.toRequest());
            output.flush();

            String response = (String) input.readObject();
            System.out.println(response);
        }
    }

    private void authenticate(ObjectOutputStream output, ObjectInputStream input)
            throws IOException, ClassNotFoundException {

        boolean authenticated = false;
        while (!authenticated) {
            Password passwordRequest = new Password(null);
            Request<String, List<String>> request = passwordRequest.toRequest();

            output.writeObject(request);
            output.flush();

            String response = (String) input.readObject();
            System.out.println(response);

            if (!response.equals("Invalid password") &&
                    !response.equals("User not found") &&
                    !response.equals("Login is busy")) {
                authenticated = true;
            }
        }
    }
}