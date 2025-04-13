package commands;

import exceptions.InvalidInputException;
import interfaces.Executable;
import interfaces.ValidatableCommand;
import main_classes.Main;
import reader_manager.InputManager;
import utility.CollectionManager;
import utility.HistoryParser;

/**
 * Удаляет все элементы коллекции, у которых значение id меньше заданного юзером (сравнение происходит как и у int'ов)
 *
 * @see CollectionManager#remove_lower(Integer)
 */
public class RemoveLower extends Command implements Executable, ValidatableCommand {
    public RemoveLower(Object parameter) {
        super(parameter);
    }

    @Override
    public boolean validate() {
        try {
            if (this.parameter.equals("{element}")) {
                return true;
            } else {
                throw new InvalidInputException("У remove_lower должен быть токен {element}!");
            }
        } catch (NullPointerException | NumberFormatException e) {
            System.out.println("У remove_lower должен быть токен {element}!");
            return false;
        }
    }

    @Override
    public void execute() {

        Main.commandsList.add("save");
        HistoryParser.parseToFile();
        if (Main.groups.size() == 0) {
            System.out.println("В коллекции нету ни одного элемента\n");
        } else {
            boolean flag = true;

            do {
                try {
                    System.out.println("Введите id объекта. Все объекты коллекции меньше введённого будут удалены:");
                    String line = InputManager.readInput();
                    if (line == null || line.trim().isEmpty()) {
                        throw new IllegalArgumentException("id объекта не может быть пустым!");
                    }

                    Integer id = Integer.parseInt(line.trim());
                    flag = false;
                    CollectionManager.remove_lower(id);
                } catch (NumberFormatException e) {
                    System.out.println("id объекта должен быть целым числом!\n");
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage() + "\n");
                }
            } while (flag);
        }

    }
}
