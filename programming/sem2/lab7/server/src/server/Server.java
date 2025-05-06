package server;

import general.Request;
import server.utility.HistoryParser;
import server.utility.MyLogger;
import server.utility.RequestRunner;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {
    private final ExecutorService clientPool = Executors.newFixedThreadPool(5);
    private final ExecutorService requestPool = Executors.newCachedThreadPool();
    private final int port;
    private volatile boolean isRunning = true;
    private final Thread consoleThread = new Thread(this::console);
    private final RequestRunner runner = new RequestRunner();

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        initConsoleListener();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(5000); // Таймаут для accept()
            MyLogger.info("Server started on port " + port);

            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    handleNewConnection(clientSocket);
                } catch (SocketTimeoutException e) {
                }
            }
        } catch (IOException e) {
            MyLogger.info("Server error: " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    private void initConsoleListener() {
        consoleThread.setDaemon(true);
        consoleThread.start();
    }

    private void handleNewConnection(Socket clientSocket) {
        clientPool.submit(() -> {
            MyLogger.info("New connection from: " + clientSocket.getRemoteSocketAddress());

            try (ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
                 ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream())) {

                clientSocket.setSoTimeout(30000);
                processClient(clientSocket, output, input);

            } catch (IOException | ClassNotFoundException e) {

            } finally {
                closeClientSocket(clientSocket);
            }
        });
    }

    private void processClient(Socket client, ObjectOutputStream output, ObjectInputStream input)
            throws IOException, ClassNotFoundException {

        while (!client.isClosed() && isRunning) {
            try {
                Request<?, ?> request = (Request<?, ?>) input.readObject();
                MyLogger.info("Received request: " + request.getCmd());

                requestPool.submit(() -> processRequest(client, output, request));

            } catch (SocketTimeoutException e) {

                if (!client.isConnected()) break;
            }
        }
    }

    private void processRequest(Socket client, ObjectOutputStream output, Request<?, ?> request) {
        try {
            String response = runner.run((Request<String, ?>) request);
            sendResponse(client, output, response);
        } catch (Exception e) {
            MyLogger.info("Request processing error: " + e.getMessage());
            sendResponse(client, output, "Error processing request");
        }
    }

    private void sendResponse(Socket client, ObjectOutputStream output, String response) {
        try {
            if (!client.isClosed()) {
                output.writeObject(response);
                output.flush();
                MyLogger.info("Sent response to " + client.getRemoteSocketAddress());
            }
        } catch (IOException e) {
            MyLogger.info("Failed to send response: " + e.getMessage());
        }
    }

    private void closeClientSocket(Socket client) {
        try {
            if (!client.isClosed()) {
                client.close();
                MyLogger.info("Client disconnected: " + client.getRemoteSocketAddress());
            }
        } catch (IOException e) {
            MyLogger.info("Error closing client socket: " + e.getMessage());
        }
    }

    private void console() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (isRunning) {
                if (scanner.hasNextLine()) {
                    String command = scanner.nextLine().trim().toLowerCase();

                    switch (command) {
                        case "exit":
                            shutdown();
                            break;
                        case "status":
                            System.out.println("Server status: " + (isRunning ? "RUNNING" : "STOPPED"));
                            break;
                        default:
                            System.out.println("Unknown command");
                    }
                }
            }
        }
    }

    private synchronized void shutdown() {
        if (!isRunning) return;

        isRunning = false;
        MyLogger.info("Shutting down server...");

        try {
            clientPool.shutdown();
            requestPool.shutdown();

            if (!clientPool.awaitTermination(10, TimeUnit.SECONDS)) {
                clientPool.shutdownNow();
            }

            if (!requestPool.awaitTermination(10, TimeUnit.SECONDS)) {
                requestPool.shutdownNow();
            }

            HistoryParser.parseToFile();
            MyLogger.info("Server shutdown complete");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            MyLogger.info("Shutdown interrupted: " + e.getMessage());
        }
    }

}