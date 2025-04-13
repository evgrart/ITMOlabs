package commands;

import exceptions.InvalidInputException;
import interfaces.Executable;
import interfaces.ValidatableCommand;
import main_classes.Main;
import utility.HistoryParser;
import utility.Parser;

/**
 * Сохраняет все элементы коллекции в json для дальнейшей инициализации коллекции в следующей сессии
 *
 * @see Parser
 * @see Main#groups
 */
public class Save extends Command implements Executable, ValidatableCommand {
    public Save(Object parameter) {
        super(parameter);
    }

    @Override
    public boolean validate() {
        try {
            if (this.parameter == null) {
                return true;
            } else {
                throw new InvalidInputException("У save не должно быть аргументов!");
            }
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public void execute() {

        Main.commandsList.add("save");
        HistoryParser.parseToFile();
        Parser.toJson();
        System.out.println();
    }
}
