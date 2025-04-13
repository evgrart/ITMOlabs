package server.commands;

import general.exceptions.InvalidInputException;
import server.interfaces.Executable;
import server.interfaces.ValidatableParameter;
import server.utility.CollectionManager;
import server.utility.HistoryParser;


/**
 * Удаляет элемент коллекции по его id
 *
 * @see CollectionManager#remove_by_id(Integer)
 * @see CollectionManager#ids
 */
public class RemoveById extends Command implements ValidatableParameter<Integer>, Executable {

    @Override
    public Integer validate(Object parameter) {
        if (parameter instanceof Integer) {
            return (Integer) parameter;
        }
        throw new InvalidInputException("Parameter of update command must be a Integer");
    }

    @Override
    public String execute(Object parameter) {
        Integer id = validate(parameter);
        CollectionManager.commandsList.add("remove_by_id");
        HistoryParser.parseToFile();

        if (CollectionManager.groups.isEmpty()) {
            return "Коллекция пуста - удалять нечего\n";
        }
        if (!CollectionManager.ids.containsKey(id)) {
            return "Объект по заданному id не найден\n";
        }
        collectionManager.remove_by_id(id);
        return "Элемент коллекции с id " + id + " был удалён\n";
    }
}
