package commands;

import exceptions.InvalidInputException;
import interfaces.Executable;
import interfaces.ValidatableCommand;
import main_classes.Main;
import utility.CollectionManager;
import utility.HistoryParser;

/**
 * Выводит информацию и коллекции: размер, дату инициализации и тип коллекции
 *
 * @see CollectionManager#info()
 */
public class Info extends Command implements Executable, ValidatableCommand {
    public Info(Object parameter) {
        super(parameter);
    }

    @Override
    public boolean validate() {
        try {
            if (this.parameter == null) {
                return true;
            } else {
                throw new InvalidInputException("У info не должно быть аргументов!");
            }
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public void execute() {

        Main.commandsList.add("info");
        HistoryParser.parseToFile();
        System.out.println("Информация о коллекции:");
        CollectionManager.info();
    }
}
