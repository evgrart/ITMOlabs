package server.commands;

import general.exceptions.InvalidInputException;
import general.main_classes.StudyGroup;
import server.interfaces.Executable;
import server.interfaces.ValidatableParameter;
import server.utility.CollectionManager;
import server.utility.HistoryParser;

import java.util.Iterator;


/**
 * Удаляет все элементы коллекции, у которых значение id меньше заданного юзером (сравнение происходит как и у int'ов)
 */
public class RemoveLower extends Command implements ValidatableParameter<Integer>, Executable {

    @Override
    public Integer validate(Object parameter) {
        if (parameter instanceof Integer) {
            return (Integer) parameter;
        }
        throw new InvalidInputException("Parameter of remove_lower command must be a Integer");
    }

    @Override
    public String execute(Object parameter) {
        Integer id = validate(parameter);
        CollectionManager.commandsList.add("remove_lower");
        HistoryParser.parseToFile();

        if (CollectionManager.groups.isEmpty()) {
            return "Коллекция пуста - удалять нечего\n";
        }

        boolean isRemoved = false;

        Iterator<StudyGroup> iterator = CollectionManager.groups.iterator();
        while (iterator.hasNext()) {
            StudyGroup el = iterator.next();
            if (el.getId() < id) {
                sb.append("Элемент с id " + el.getId() + " был удалён");
                iterator.remove();
                collectionManager.ids.remove(el.getId());
                isRemoved = true;
            }
        }
        if (!isRemoved) {
            return "Элементов с меньшим id нет\n";
        }
        return sb.toString() + "\n";

    }
}
