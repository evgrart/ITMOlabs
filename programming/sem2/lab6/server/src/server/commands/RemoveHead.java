
package server.commands;

import general.exceptions.InvalidInputException;
import server.interfaces.Executable;
import server.interfaces.ValidatableParameter;
import server.utility.CollectionManager;
import server.utility.HistoryParser;


/**
 * Выводит и удаляет первый элемент коллекции (с индексом 0)
 *
 * @see CollectionManager#remove_head()
 */
public class RemoveHead extends Command implements ValidatableParameter<Void>, Executable {

    @Override
    public Void validate(Object parameter) {
        if (!(parameter == null)) {
            throw new InvalidInputException("Parameter of remove_head command must be a null");
        }
        return null;
    }

    public String execute(Object parameter) {
        try {
            validate(parameter);
            CollectionManager.commandsList.add("remove_head");
            HistoryParser.parseToFile();

            if (CollectionManager.groups.isEmpty()) {
                return "Коллекция пуста - удалять нечего\n";
            }
            return collectionManager.remove_head() + "\n";
        } catch (InvalidInputException e) {
            return e.getMessage();
        }
    }
}
