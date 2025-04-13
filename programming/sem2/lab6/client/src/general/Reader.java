package general;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Reader {
    public static Object reader(SocketChannel channel) throws IOException, ClassNotFoundException {

        ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
        int bytesRead = channel.read(sizeBuffer); // cчитываем размер
        if (bytesRead == -1) {
            return null;
        }
        sizeBuffer.flip();
        int dataSize = sizeBuffer.getInt(); // извлекаем размер данных

        ByteBuffer dataBuffer = ByteBuffer.allocate(dataSize);
        int totalRead = 0;

        // может быть несколько итераций чтения
        while (totalRead < dataSize) {
            bytesRead = channel.read(dataBuffer);
            if (bytesRead == -1) {
                return null;
            }
            totalRead += bytesRead;
        }

        dataBuffer.flip();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(dataBuffer.array());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream); // десериализуем

        Object receivedObject = objectInputStream.readObject();
        return receivedObject;
    }
}
