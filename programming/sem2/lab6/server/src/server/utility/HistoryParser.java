package server.utility;


import java.io.*;
import java.util.List;

/**
 * Сериализует в {@link CollectionManager#commandsList} / десериализует в файлик .txt (для хранения истории за предыдущие сеансы) предыдущие команды юзера
 *
 * @see server.commands.History
 */

public class HistoryParser {

    /**
     * Десериализация в {@link CollectionManager#commandsList}
     */
    public static void parseToList() {
        File file = new File("/home/evgrart/IdeaProjects/server/src/server/utility/history.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    CollectionManager.commandsList.add(line);
                }
            }
        } catch (IOException e) {
            MyLogger.info("Ошибка при чтении файла: " + e.getMessage());
        }
    }

    /**
     * Парсим из {@link CollectionManager#commandsList} в файл-буфер
     */
    public static void parseToFile() {
        File file = new File("/home/evgrart/IdeaProjects/server/src/server/utility/history.txt");

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            List<String> commands = CollectionManager.commandsList;

            for (String command : commands) {
                writer.println(command);
            }
        } catch (IOException e) {
            MyLogger.info("Ошибка при записи в файл: " + e.getMessage());
        }
    }
}
