package client;

import client.input_managers.InputManager;
import general.exceptions.InvalidInputException;
import general.main_classes.Person;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class PersonCreature {
    public PersonCreature() {
    }

    public static Person createPerson() {
        Person.PersonBuilder person = Person.builder();
        boolean flag = true;

        do {
            try {
                System.out.print("Введите имя groupAdmin: ");
                String consoleRead = InputManager.readInput();
                if (consoleRead == null || consoleRead.isEmpty()) {
                    throw new InvalidInputException("Поле name должно быть отличным от null и пустой строки!");
                }

                person.name(consoleRead);
                flag = false;
            } catch (InvalidInputException e) {
                System.out.println(e.getMessage());
            }
        } while(flag);

        flag = true;

        do {
            try {
                System.out.print("Введите дату и время в формате yyyy-MM-dd HH:mm:ss: ");
                String consoleRead = InputManager.readInput();
                if (consoleRead == null) {
                    throw new InvalidInputException("");
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime date = LocalDateTime.parse(consoleRead, formatter);
                person.birthday(date);
                flag = false;
            } catch (InvalidInputException | DateTimeParseException var5) {
                System.out.println("Введите корректную дату и время в формате yyyy-MM-dd HH:mm:ss!");
            }
        } while(flag);

        flag = true;

        do {
            try {
                System.out.print("Введите значение height: ");
                String consoleRead = InputManager.readInput();
                Long height = Long.parseLong(consoleRead);
                if (height <= 0L || height > Long.MAX_VALUE) {
                    throw new InvalidInputException("");
                }

                person.height(height);
                flag = false;
            } catch (InvalidInputException | NumberFormatException var8) {
                System.out.println("height должно быть положительным числом типа long (до 2^31 - 1) и не может быть null!");
            }
        } while(flag);

        flag = true;

        do {
            try {
                System.out.print("Введите значение weight: ");
                String consoleRead = InputManager.readInput();
                Integer weight = Integer.parseInt(consoleRead);
                if (weight <= 0 || weight > Integer.MAX_VALUE) {
                    throw new InvalidInputException("");
                }

                person.weight(weight);
                flag = false;
            } catch (InvalidInputException | NumberFormatException var7) {
                System.out.println("weight должно быть положительным числом типа int (до 2^15 - 1) и не может быть null!");
            }
        } while(flag);

        flag = true;

        do {
            try {
                System.out.print("Введите значение passportID: ");
                String consoleRead = InputManager.readInput();
                if (consoleRead == null || consoleRead.isEmpty()) {
                    throw new InvalidInputException("Поле passportID должно быть отличным от null и пустой строки!");
                }

                if (consoleRead.length() > 47) {
                    throw new InvalidInputException("Длина passportID должна быть не больше 47");
                }

                flag = false;
                person.passportID(consoleRead);
            } catch (InvalidInputException | NumberFormatException e) {
                System.out.println(e.getMessage());
            }
        } while(flag);

        return person.build();
    }
}
