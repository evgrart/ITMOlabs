package server.commands;

import general.exceptions.InvalidInputException;
import server.interfaces.Executable;
import server.interfaces.ValidatableParameter;
import server.utility.CollectionManager;
import server.utility.HistoryParser;


/**
 * Выводит информацию и коллекции: размер, дату инициализации и тип коллекции
 *
 * @see CollectionManager#info()
 */
public class Info extends Command implements ValidatableParameter<Void>, Executable {

    @Override
    public Void validate(Object parameter) {
        if (!(parameter == null)) {
            throw new InvalidInputException("Parameter of info command must be a null");
        }
        return null;
    }

    @Override
    public String execute(Object parameter) {
        try {
            validate(parameter);
            CollectionManager.commandsList.add("info");
            HistoryParser.parseToFile();

            return collectionManager.info();
        } catch (InvalidInputException e) {
            return e.getMessage();
        }
    }
}
