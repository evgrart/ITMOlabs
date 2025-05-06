package client.input_managers;

import general.ClientCommand;
import general.exceptions.InvalidInputException;

public class Reader {
    public Reader() {
    }

    public static ClientCommand getLine(String read) {
        String[] line = read.split(" ");

        try {
            if (!line[0].equals("")) {
                if (line.length == 1) {
                    return toCommand(line[0], (String)null);
                }

                if (line.length == 2) {
                    if (line[0].equals("update")) {
                        System.out.println("Добавьте токен {element}");
                        InputManager.startInput();
                    }

                    return toCommand(line[0], line[1]);
                }

                if (line[0].equals("update") && line[1].matches("\\d+") && line[2].equals("{element}")) {
                    return toCommand(line[0], line[1]);
                }

                if (line[0].equals("update")) {
                    throw new InvalidInputException("Команда update должна быть вида update id {element}, где id - число");
                }

                throw new InvalidInputException("Такой длинной команды нет, используйте help для вывода списка команд\n");
            }

            InputManager.startInput();
        } catch (InvalidInputException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    private static ClientCommand toCommand(String command, String parameter) {
        CommandManager executableCommand = new CommandManager(parameter);
        return executableCommand.runCommand(command);
    }
}
