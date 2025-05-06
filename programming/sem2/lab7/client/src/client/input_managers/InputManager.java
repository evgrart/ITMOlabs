package client.input_managers;

import general.ClientCommand;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class InputManager {
    public static Scanner consoleRead;
    private static final InputStream DEFAULT_IN;

    public InputManager() {
    }

    public static String readInput() {
        if (consoleRead.hasNextLine()) {
            String input = consoleRead.nextLine().strip();
            if (input.equals("exit")) {
                System.out.println("CLient is closed");
                System.exit(0);
            }

            return input;
        } else {
            restoreStandardInput();
            return consoleRead.nextLine();
        }
    }

    public static ClientCommand startInput() {
        try {
            String ConsoleRead = readInput();
            return Reader.getLine(ConsoleRead);
        } catch (NoSuchElementException var1) {
            System.out.println("Ну зачем вам ломать мою программу(");
            System.exit(0);
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public static void restoreStandardInput() {
        System.setIn(DEFAULT_IN);
        consoleRead = new Scanner(System.in);
    }

    static {
        consoleRead = new Scanner(System.in);
        DEFAULT_IN = System.in;
    }
}
