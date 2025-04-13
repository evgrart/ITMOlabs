package client;


import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {
    public static String file;

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                System.out.println("Перезапустите программу и укажите путь к файлу!");
                System.exit(0);
            }
            file = args[0];
            int port = 8081;
            Client client = new Client(InetAddress.getByName("localhost"), port);
            client.run();
        } catch (UnknownHostException e) {
            System.out.println("Host with this name could not be found");
        }
    }
}


