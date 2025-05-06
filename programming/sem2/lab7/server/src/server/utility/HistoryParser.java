//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package server.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class HistoryParser {
    public static ArrayList<String> commandsList = new ArrayList();

    public HistoryParser() {
    }

    public static void parseToList() {
        File file = new File("src/server/utility/history.txt");
        // /home/studs/s465826/history.txt

        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    commandsList.add(line);
                }
            }
        } catch (IOException e) {
            MyLogger.info("Ошибка при чтении файла: " + e.getMessage());
        }

    }

    public static void parseToFile() {
        File file = new File("src/server/utility/history.txt");

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for(String command : commandsList) {
                writer.println(command);
            }
        } catch (IOException e) {
            MyLogger.info("Ошибка при записи в файл: " + e.getMessage());
        }

    }
}
