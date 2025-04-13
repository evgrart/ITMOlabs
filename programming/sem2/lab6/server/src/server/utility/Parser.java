package server.utility;

import general.main_classes.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static server.utility.CollectionManager.groups;

/**
 * Парсер из/в json
 *
 * @see server.commands.Save
 */
public class Parser {

    /**
     * Парсим в json
     */
    public static void toJson() {
        StringBuilder json = new StringBuilder("[");

        for (int i = 0; i < groups.size(); i++) {
            json.append(groups.get(i).toJson());
            if (i < groups.size() - 1) {
                json.append(",");
            }
        }
        json.append("\n]");

        try (PrintWriter writer = new PrintWriter(CollectionManager.FILE_NAME)) {
            writer.println(json);
            MyLogger.info("Коллекция записана в json");
        } catch (FileNotFoundException e) {
            MyLogger.info("Ошибка при записи в файл: " + e.getMessage());
        }
    }

    /**
     * Парсим из json
     */
    public static void fromJson() {
        CollectionManager collectionManager = new CollectionManager();
        StudyGroup.StudyGroupBuilder group = null;
        Coordinates.CoordinatesBuilder coordinates = null;
        Person.PersonBuilder person = Person.builder();
        StringBuilder file = new StringBuilder();

        if (CollectionManager.FILE_NAME == null || !new File(CollectionManager.FILE_NAME).exists()) {
            MyLogger.info("Ошибка: файл не существует.");
            System.exit(0);
        } else if (new File(CollectionManager.FILE_NAME).length() == 0) {
            try (PrintWriter writer = new PrintWriter(CollectionManager.FILE_NAME)) {
                writer.println("[" + "\n" + "]");
            } catch (FileNotFoundException e) {
                MyLogger.info("Ошибка при записи в файл: " + e.getMessage());
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(CollectionManager.FILE_NAME))) {
            String line;
            while (!(line = reader.readLine()).equals("]")) {
                file.append(line.trim());

            }
        } catch (IOException e) {
            MyLogger.info("Ошибка при чтении файла: " + e.getMessage());
            MyLogger.info("Раздайте права!");
            MyLogger.info("Программа завершена");
            System.exit(0);
        }
        String[] json = String.valueOf(file).substring(1).trim().split(",");
        if (json.length > 1) {
            for (String s : json) {
                String key = s.split(":", 2)[0].trim().replaceAll("[{},:\"]", "");
                String value = s.split(":", 2)[1].trim();
                if (key.equals("id")) {
                    group = StudyGroup.builder();
                    group.id(Integer.parseInt(value));
                } else {
                    switch (key) {
                        case "name":
                            group.name(value.substring(1, value.length() - 1).trim());
                            break;
                        case "coordinates":
                            coordinates = Coordinates.builder();
                            coordinates.x(Double.parseDouble(value.split(" ")[1].trim()));
                            break;
                        case "y":
                            coordinates.y(Double.parseDouble(value.substring(0, value.length() - 1)));
                            group.coordinates(coordinates.build());
                            break;
                        case "creationDate":
                            ZonedDateTime date = ZonedDateTime.parse(value.substring(1, value.length() - 1));
                            group.creationDate(date);
                            break;
                        case "studentsCount":
                            group.studentsCount(Integer.parseInt(value));
                            break;
                        case "expelledStudents":
                            group.expelledStudents(Integer.parseInt(value));
                            break;
                        case "shouldBeExpelled":
                            group.shouldBeExpelled(Integer.parseInt(value));
                            break;
                        case "formOfEducation":
                            if (value.equals("null")) {
                            } else {
                                group.formOfEducation(FormOfEducation.valueOf(value.substring(1, value.length() - 1)));
                            }
                            break;
                        case "groupAdmin":
                            if (!value.equals("null}")) {
                                person.name(value.split(":")[1].replaceAll("[\"]", "").trim());
                            } else {
                                collectionManager.add(group.build());
                            }
                            break;
                        case "birthday":
                            String line = value.split("T")[0] + " " + value.split("T")[1];
                            LocalDateTime dateTime = LocalDateTime.parse(line.substring(1, line.length() - 1), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                            person.birthday(dateTime);
                            break;
                        case "height":
                            person.height(Integer.parseInt(value));
                            break;
                        case "weight":
                            person.weight(Integer.parseInt(value));
                            break;
                        case "passportID":
                            person.passportID(value.substring(1, value.length() - 3));
                            group.groupAdmin(person.build());
                            collectionManager.add(group.build());
                            break;
                    }
                }
            }
        }
    }
}

