import java.util.Scanner;

public class Hamming {
    private final byte[] chars = new byte[7];
    private final String[] symbols = {"r1", "r2", "i1", "r3", "i2", "i3", "i4"};
    private final byte s1;
    private final byte s2;
    private final byte s3;
    private final String index;
    private final int SysIndex;

    Hamming(String nums) {
        for (int i = 0; i <= 6; i++) {
            chars[i] = (byte) (nums.charAt(i) - '0');
        }

        s1 = (byte) (chars[0] ^ chars[2] ^ chars[4] ^ chars[6]);
        s2 = (byte) (chars[1] ^ chars[2] ^ chars[5] ^ chars[6]);
        s3 = (byte) (chars[3] ^ chars[4] ^ chars[5] ^ chars[6]);
        index = Byte.toString(s1) + s2 + s3;
        SysIndex = (index.charAt(0) - '0') +
                   (index.charAt(1) - '0') * 2 +
                   (index.charAt(2) - '0') * 4;
    }

    void PrintIndex() {
        if (SysIndex == 0) {
            System.out.println("No errors");
        } else {
            System.out.println("Wrong bit is " + symbols[SysIndex - 1]);
        }
    }

    void PrintCorrectly() {
        if (SysIndex == 0) {
            System.out.println("The message is correct");
        } else {
            chars[SysIndex - 1] = (byte) (1 - chars[SysIndex - 1]);
            System.out.print("Correct message is: ");
            System.out.println("" + chars[2] + chars[4] + chars[5] + chars[6]);
        }
    }

    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);
        String name = console.nextLine();
        Hamming m = new Hamming(name);
        m.PrintCorrectly();
        m.PrintIndex();
    }
}
