package utility;

import exceptions.InvalidInputException;
import main_classes.Person;
import main_classes.Person.PersonBuilder;
import main_classes.StudyGroup;
import reader_manager.InputManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Класс для инициализации объекта Person (используется в {@link GroupCreature})
 *
 * @see StudyGroup
 */
public class PersonCreature {
    /**
     * @return возвращает инициализированный юзером Person
     * @exception InvalidInputException если поле, введённое юзером, не валидно
     */
    public static Person createPerson() {
        PersonBuilder person = Person.builder();
        boolean flag = true;

        String consoleRead;
        do {
            try {
                System.out.print("Введите имя groupAdmin: ");
                consoleRead = InputManager.readInput();
                if (consoleRead == null || consoleRead.isEmpty()) {
                    throw new InvalidInputException("Поле name должно быть отличным от null и пустой строки!");
                }

                person.name(consoleRead);
                flag = false;
            } catch (InvalidInputException e) {
                System.out.println(e.getMessage());
            }
        } while (flag);

        flag = true;

        do {
            try {
                System.out.print("Введите дату и время в формате yyyy-MM-dd HH:mm:ss: ");
                consoleRead = InputManager.readInput();
                if (consoleRead == null) {
                    throw new InvalidInputException("");
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime date = LocalDateTime.parse(consoleRead, formatter);
                person.birthday(date);
                flag = false;
            } catch (DateTimeParseException | InvalidInputException e) {
                System.out.println("Введите корректную дату и время в формате yyyy-MM-dd HH:mm:ss!");
            }
        } while (flag);

        flag = true;

        do {
            try {
                System.out.print("Введите значение height: ");
                consoleRead = InputManager.readInput();
                Long height = Long.parseLong(consoleRead);
                if (height <= 0 || height > Long.MAX_VALUE) {
                    throw new InvalidInputException("");
                }

                person.height(height);
                flag = false;
            } catch (NumberFormatException | InvalidInputException e) {
                System.out.println("height должно быть положительным числом типа long (до 2^31 - 1) и не может быть null!");
            }
        } while (flag);

        flag = true;

        do {
            try {
                System.out.print("Введите значение weight: ");
                consoleRead = InputManager.readInput();
                Integer weight = Integer.parseInt(consoleRead);
                if (weight <= 0 || weight > Integer.MAX_VALUE) {
                    throw new InvalidInputException("");
                }

                person.weight(weight);
                flag = false;
            } catch (NumberFormatException | InvalidInputException e) {
                System.out.println("weight должно быть положительным числом типа int (до 2^15 - 1) и не может быть null!");
            }
        } while (flag);

        flag = true;

        do {
            try {
                System.out.print("Введите значение passportID: ");
                consoleRead = InputManager.readInput();
                if (consoleRead == null || consoleRead.isEmpty()) {
                    throw new InvalidInputException("Поле passportID должно быть отличным от null и пустой строки!");
                }

                if (consoleRead.length() > 47) {
                    throw new InvalidInputException("Длина passportID должна быть не больше 47");
                }

                flag = false;
                person.passportID(consoleRead);
            } catch (NumberFormatException | InvalidInputException e) {
                System.out.println(e.getMessage());
            }
        } while (flag);

        System.out.println("Создание groupAdmin завершено!");
        return person.build();
    }
}