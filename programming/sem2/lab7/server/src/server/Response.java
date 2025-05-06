package server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Response {
    public Response() {
    }

    public static void send(Socket client, String answer, ObjectOutputStream output) {
        (new Thread(() -> {
            try {
                output.writeObject(answer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        })).start();
    }
}
