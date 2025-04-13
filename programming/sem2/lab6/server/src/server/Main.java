package server;


import server.utility.MyLogger;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        int port = 8081;
        Server server = new Server(port);
        try {
            server.run();
        } catch (IOException e) {
            MyLogger.info("Host with this name does not exist");
        }
    }
}
