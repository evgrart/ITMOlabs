package server.commands;

import general.exceptions.InvalidInputException;
import server.interfaces.Executable;
import server.interfaces.ValidatableParameter;
import server.utility.CollectionManager;
import server.utility.HistoryParser;


/**
 * Показывает последние 8 введённых юзером команд
 */
public class History extends Command implements ValidatableParameter<Void>, Executable {
    private int startIndex;

    @Override
    public Void validate(Object parameter) {
        if (!(parameter == null)) {
            throw new InvalidInputException("Parameter of history command must be a null");
        }
        return null;
    }

    @Override
    public String execute(Object parameter) {
        try {
            startIndex = Math.max(0, collectionManager.commandsList.size() - 7);
            sb.setLength(0);
            validate(parameter);
            CollectionManager.commandsList.add("history");
            HistoryParser.parseToFile();
            sb.append("\n");
            for (int i = collectionManager.commandsList.size() - 1; i >= this.startIndex; --i) {
                sb.append(collectionManager.commandsList.get(i) + "\n");
            }
            return sb.toString();
        } catch (InvalidInputException e) {
            return e.getMessage();
        }
    }
}
