package reader_manager;


import exceptions.InvalidInputException;

/**
 * Обработчик команд
 */
public class Reader {
    /**
     * @param read обрабатываемая строка
     * @throws InvalidInputException если команда, введённая юзером, не валидна
     */
    public static void getLine(String read) {
        String[] line = read.split(" ");
        try {
            if (line[0].equals("")) {
                InputManager.startInput();
            } else if (line.length == 1) {
                Reader.toCommand(line[0], null);
            } else if (line.length == 2) {
                if (line[0].equals("update")) {
                    System.out.println("Добавьте токен {element}");
                    InputManager.startInput();
                }
                Reader.toCommand(line[0], line[1]);
            } else {
                if (line[0].equals("update") && line[1].matches("\\d+") && line[2].equals("{element}")) {
                    Reader.toCommand(line[0], line[1]);
                } else if (line[0].equals("update")) {
                    throw new InvalidInputException("Команда update должна быть вида update id {element}, где id - число");
                } else {
                    throw new InvalidInputException("Такой длинной команды нет, используйте help для вывода списка команд");
                }
            }
        } catch (InvalidInputException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void toCommand(String command, Object parameter) {
        CommandManager executableCommand = new CommandManager(parameter);
        executableCommand.runCommand(command);
    }
}