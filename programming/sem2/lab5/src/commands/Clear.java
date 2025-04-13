package commands;

import exceptions.InvalidInputException;
import interfaces.Executable;
import interfaces.ValidatableCommand;
import main_classes.Main;
import utility.CollectionManager;
import utility.HistoryParser;

/**
 * Очищает коллекцию
 *
 * @see CollectionManager#clear()
 */
public class Clear extends Command implements Executable, ValidatableCommand {
    public Clear(Object parameter) {
        super(parameter);
    }

    @Override
    public boolean validate() {
        try {
            if (this.parameter == null) {
                return true;
            } else {
                throw new InvalidInputException("У clear не должно быть аргументов!");
            }
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public void execute() {

        Main.commandsList.add("clear");
        HistoryParser.parseToFile();
        System.out.println("Коллекция была очищена!\n");
        CollectionManager.clear();
    }
}