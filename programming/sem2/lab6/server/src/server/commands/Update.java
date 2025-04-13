package server.commands;

import general.Tuple;
import general.exceptions.InvalidInputException;
import general.main_classes.StudyGroup;
import server.interfaces.Executable;
import server.interfaces.ValidatableParameter;
import server.utility.CollectionManager;
import server.utility.HistoryParser;


/**
 * Обновляет элемент коллекции на заданный юзером
 *
 * @see CollectionManager#update
 */
public class Update extends Command implements ValidatableParameter<Tuple>, Executable {

    @Override
    public Tuple<Integer, StudyGroup.StudyGroupBuilder> validate(Object parameter) {
        if (parameter instanceof Tuple) {
            if (((Tuple<?, ?>) parameter).getFirst() instanceof Integer && ((Tuple<?, ?>) parameter).getSecond() instanceof StudyGroup.StudyGroupBuilder) {}
            return (Tuple<Integer, StudyGroup.StudyGroupBuilder>) parameter;
        }
        throw new InvalidInputException("Parameter of update command must be a Integer");
    }

    @Override
    public String execute(Object parameter) {
        Tuple<Integer, StudyGroup.StudyGroupBuilder> tuple = this.validate(parameter);
        CollectionManager.commandsList.add("update");
        HistoryParser.parseToFile();

        Integer id = tuple.getFirst();
        StudyGroup group = tuple.getSecond().build();
        return collectionManager.update(id, group);
    }
}
