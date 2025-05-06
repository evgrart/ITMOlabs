package server;

public class Main {
    public Main() {
    }

    public static void main(String[] args) {
        int port = 8084;
        Server server = new Server(port);

        server.start();

    }
}
