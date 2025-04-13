package client.input_managers;

import general.ClientCommand;

import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Обработчик ввода
 */
public class InputManager {
    public static Scanner consoleRead = new Scanner(System.in);
    private static final InputStream DEFAULT_IN = System.in;

    /**
     * Читает строку ввода из текущего источника (консоль или файл).
     * Если данные отсутствуют, восстанавливает ввод на стандартную консоль.
     *
     * @return Введенная строка
     */
    public static String readInput() {
        if (consoleRead.hasNextLine()) {
            String input = consoleRead.nextLine().strip();
            if (input.equals("exit")) {
                System.out.println("CLient is closed");
                System.exit(0);
            }
            return input; // Читаем строку из текущего ввода (файл или консоль)
        } else {
            restoreStandardInput(); // Возвращаем ввод на клавиатуру
            return consoleRead.nextLine(); // Читаем с клавиатуры
        }
    }

    public static ClientCommand startInput() {
        try {
            do {
                String ConsoleRead = readInput();
                return Reader.getLine(ConsoleRead);
            } while (true);
        } catch (NoSuchElementException e) {
            System.out.println("Ну зачем вам ломать мою программу(");
            System.exit(0);
        }
        return null;
    }

    /**
     * Восстанавливает стандартный ввод с клавиатуры.
     */
    public static void restoreStandardInput() {
        System.setIn(DEFAULT_IN);
        InputManager.consoleRead = new Scanner(System.in);
    }
}