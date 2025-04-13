import java.util.Random;
import static java.lang.Math.*;

public class Lab {
    private static void generate(long[] z) {
        for (int i = 0; i <= 6; i++) {
            z[i] = 17 - i * 2;
        }
    }

    private static void generate(double[] x, Random random) {
        for (int i = 0; i < 10; i++) {
            x[i] = -13.0 + 22.0 * random.nextDouble();
        }
    }

    private static void generate(double[][] z1, double[] x, long[] z) {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 10; j++) {
                switch ((int) z[i]) {
                    case 9:
                        z1[i][j] = 2 * atan(1 / (pow(E, abs(x[j]))));
                        break;
                    case 5, 7, 15:
                        z1[i][j] = tan(pow(pow((x[j] / (1 - x[j])), 2), pow(0.25 * (x[j] + 1), 2)));
                        break;
                    default:
                        z1[i][j] = cos((3.0 / 4.0) / (tan(cos(x[j])) - 1));
                }
            }
        }
    }

    private static void printing(double[][] mass) {
        for (double[] row : mass) {
            for (double el : row) {
                System.out.printf("%9.5f", el);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Random random = new Random();
        long[] z = new long[7];
        double[] x = new double[10];
        double[][] z1 = new double[7][10];

        generate(z);
        generate(x, random);
        generate(z1, x, z);
        printing(z1);
    }
}
