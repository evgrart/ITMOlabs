package server;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Response {
    public static void send(SocketChannel client, String answer) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(answer); // сериализуем и записываем в byteArrayOutputStream
            objectOutputStream.flush();
            byte[] data = byteArrayOutputStream.toByteArray();

            ByteBuffer byteSizeBuffer = ByteBuffer.allocate(4);
            byteSizeBuffer.putInt(data.length);
            byteSizeBuffer.flip();
            client.write(byteSizeBuffer); // отправляем размер ответа

            ByteBuffer buffer = ByteBuffer.wrap(data);
            client.write(buffer); // отправляем ответ
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
