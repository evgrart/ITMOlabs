package client;

public class Main {
    public Main() {
    }

    public static void main(String[] args) {
        int port = 8084;
        Client client = new Client(port);
        client.run();
    }
}
