package server.commands;

import general.exceptions.InvalidInputException;
import server.interfaces.Executable;
import server.interfaces.ValidatableParameter;
import general.main_classes.StudyGroup;
import server.utility.CollectionManager;
import server.utility.HistoryParser;


/**
 * Выводит информацию (поля) каждого элементы коллекции
 *
 * @see CollectionManager#show(StudyGroup)
 */
public class Show extends Command implements ValidatableParameter<Void>, Executable {

    @Override
    public Void validate(Object parameter) {
        if (!(parameter == null)) {
            throw new InvalidInputException("Parameter of show command must be a null");
        }
        return null;
    }

    @Override
    public String execute(Object parameter) {
        try {
            validate(parameter);
            CollectionManager.commandsList.add("show");
            HistoryParser.parseToFile();

            sb.setLength(0);
            if (CollectionManager.groups.isEmpty()) {
                return "В коллекции нету ни одного элемента\n";
            } else {
                for (StudyGroup el : collectionManager.groups) {
                    sb.append("Информация об объекте c индексом " + collectionManager.groups.indexOf(el) + " в коллекции\n");
                    sb.append(collectionManager.show(el) + "\n\n");
                }
                return sb.toString();
            }
        } catch (InvalidInputException e) {
            return e.getMessage();
        }
    }

}
