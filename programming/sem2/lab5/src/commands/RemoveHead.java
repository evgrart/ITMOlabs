
package commands;

import exceptions.InvalidInputException;
import interfaces.Executable;
import interfaces.ValidatableCommand;
import main_classes.Main;
import utility.CollectionManager;
import utility.HistoryParser;

/**
 * Выводит и удаляет (аналог .pop) первый элемент коллекции (с индексом 0)
 *
 * @see CollectionManager#remove_head()
 */
public class RemoveHead extends Command implements Executable, ValidatableCommand {
    public RemoveHead(Object parameter) {
        super(parameter);
    }

    @Override
    public boolean validate() {
        if (Main.groups.size() == 0) {
            System.out.println("В коллекции нету ни одного элемента\n");
            return false;
        } else {
            try {
                if (this.parameter == null) {
                    return true;
                } else {
                    throw new InvalidInputException("У remove_head не должно быть аргументов!");
                }
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
                return false;
            }
        }
    }

    @Override
    public void execute() {

        Main.commandsList.add("remove_head");
        HistoryParser.parseToFile();
        System.out.println("Информация о первом элементе коллекции (после вывода он будет удалён):");
        CollectionManager.remove_head();
        System.out.println();
    }
}
