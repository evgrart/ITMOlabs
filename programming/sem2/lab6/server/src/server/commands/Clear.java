package server.commands;

import general.exceptions.InvalidInputException;
import server.interfaces.Executable;
import server.interfaces.ValidatableParameter;
import server.utility.CollectionManager;
import server.utility.HistoryParser;


/**
 * Очищает коллекцию
 *
 * @see CollectionManager#clear()
 */
public class Clear extends Command implements ValidatableParameter<Void>, Executable {

    @Override
    public Void validate(Object parameter) {
        if (!(parameter == null)) {
            throw new InvalidInputException("Parameter of clear command must be a null");
        }
        return null;
    }

    @Override
    public String execute(Object parameter) {
        try {
            validate(parameter);
            CollectionManager.commandsList.add("clear");
            HistoryParser.parseToFile();

            collectionManager.clear();
            return "Collection is cleared\n";
        } catch (InvalidInputException e) {
            return e.getMessage();
        }
    }
}