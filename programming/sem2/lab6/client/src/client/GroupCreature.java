package client;

import general.exceptions.InvalidInputException;
import general.main_classes.StudyGroup;
import general.main_classes.*;
import client.input_managers.InputManager;


public class GroupCreature {

    public static StudyGroup.StudyGroupBuilder createGroup() {
        boolean flag = true;
        StudyGroup.StudyGroupBuilder group = general.main_classes.StudyGroup.builder();
        String consoleRead;
        do {
            try {
                System.out.print("Введите имя группы: ");
                consoleRead = InputManager.readInput();
                if (consoleRead == null || consoleRead.isEmpty()) {
                    throw new InvalidInputException("Поле name должно быть отличным от null и пустой строки!");
                }

                group.name(consoleRead);
                flag = false;
            } catch (InvalidInputException e) {
                System.out.println(e.getMessage());
            }
        } while (flag);

        Coordinates.CoordinatesBuilder coordinates = Coordinates.builder();
        flag = true;

        Double x;
        do {
            try {
                System.out.print("Введите координату x группы: ");
                consoleRead = InputManager.readInput();
                x = Double.parseDouble(consoleRead);
                if (x < Double.MIN_VALUE || x > Double.MAX_VALUE) {
                    throw new InvalidInputException("");
                }

                coordinates.x(x);
                flag = false;
            } catch (NumberFormatException | InvalidInputException e) {
                System.out.println("Координата должна быть типа double (>= -2^63 и <= 2^63 - 1) и не могут быть null!");
            }
        } while (flag);

        flag = true;

        Double y;
        do {
            try {
                System.out.print("Введите координату y группы: ");
                consoleRead = InputManager.readInput();
                y = Double.parseDouble(consoleRead);
                if (y < Double.MIN_VALUE || y > Double.MAX_VALUE) {
                    throw new InvalidInputException("");
                }

                coordinates.y(y);
                flag = false;
                group.coordinates(coordinates.build());
            } catch (NumberFormatException | InvalidInputException e) {
                System.out.println("Координата должна быть типа double (>= -2^63 и <= 2^63 - 1) и не могут быть null!");
            }
        } while (flag);

        flag = true;

        do {
            try {
                System.out.print("Введите значение studentsCount группы: ");
                consoleRead = InputManager.readInput();
                Long studentsCount = Long.parseLong(consoleRead);
                if (studentsCount <= 0L || studentsCount > Long.MAX_VALUE) {
                    throw new InvalidInputException("Поле studentsCount должно быть целым положительным числом до 2^31 - 1 (тип long)!");
                }

                group.studentsCount(studentsCount);
                flag = false;
            } catch (NumberFormatException | InvalidInputException e) {
                System.out.println("Введите корректное положительное целое число до 2^31 - 1 (тип long)!");
            }
        } while (flag);

        flag = true;

        do {
            try {
                System.out.print("Введите значение expelledStudents группы: ");
                consoleRead = InputManager.readInput();
                Integer expelledStudents = Integer.parseInt(consoleRead);
                if (expelledStudents <= 0 || expelledStudents > Integer.MAX_VALUE) {
                    throw new InvalidInputException("Поле expelledStudents должно быть целым положительным числом!");
                }

                group.expelledStudents(expelledStudents);
                flag = false;
            } catch (NumberFormatException | InvalidInputException e) {
                System.out.println("Введите корректное положительное целое число!");
            }
        } while (flag);

        flag = true;

        do {
            try {
                System.out.print("Введите значение shouldBeExpelled группы: ");
                consoleRead = InputManager.readInput();
                Long shouldBeExpelled = Long.parseLong(consoleRead);
                if (shouldBeExpelled <= 0L || shouldBeExpelled > Long.MAX_VALUE) {
                    throw new InvalidInputException("Поле shouldBeExpelled должно быть целым положительным числом до 2^31 - 1 (тип long)!");
                }

                group.shouldBeExpelled(shouldBeExpelled);
                flag = false;
            } catch (NumberFormatException | InvalidInputException e) {
                System.out.println("Введите корректное положительное целое число до 2^31 - 1 (тип long)!");
            }
        } while (flag);

        flag = true;

        do {
            try {
                System.out.print("Введите значение formOfEducation группы (DISTANCE_EDUCATION, FULL_TIME_EDUCATION, EVENING_CLASSES): ");
                consoleRead = InputManager.readInput();
                if (consoleRead.isEmpty()) {
                    group.formOfEducation(null);
                } else {
                    group.formOfEducation(FormOfEducation.valueOf(consoleRead));
                }

                flag = false;
            } catch (IllegalArgumentException e) {
                System.out.println("Такого значения нет!");
            }
        } while (flag);

        System.out.print("Нажмите enter, если НЕ хотите создать groupAdmin (иначе - любой символ): ");
        consoleRead = InputManager.readInput();
        if (!consoleRead.isEmpty()) {
            group.groupAdmin(PersonCreature.createPerson());
        }

        return group;
    }
}