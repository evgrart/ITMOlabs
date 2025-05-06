//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package general;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Reader {
    public Reader() {
    }

    public static Object reader(SocketChannel channel) throws IOException, ClassNotFoundException {
        ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
        int bytesRead = channel.read(sizeBuffer);
        if (bytesRead == -1) {
            return null;
        } else {
            sizeBuffer.flip();
            int dataSize = sizeBuffer.getInt();
            ByteBuffer dataBuffer = ByteBuffer.allocate(dataSize);

            for(int totalRead = 0; totalRead < dataSize; totalRead += bytesRead) {
                bytesRead = channel.read(dataBuffer);
                if (bytesRead == -1) {
                    return null;
                }
            }

            dataBuffer.flip();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(dataBuffer.array());
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object receivedObject = objectInputStream.readObject();
            return receivedObject;
        }
    }
}
