package server.commands;

import general.exceptions.InvalidInputException;
import server.interfaces.Executable;
import server.interfaces.ValidatableParameter;
import server.utility.CollectionManager;
import general.main_classes.StudyGroup;
import server.utility.HistoryParser;

/**
 * Добавляет элемент в коллекцию
 *
 * @see CollectionManager#add(StudyGroup)
 */
public class Add extends Command implements ValidatableParameter<StudyGroup>, Executable {
    private StudyGroup group;

    @Override
    public StudyGroup validate(Object parameter) {
        if (parameter instanceof StudyGroup.StudyGroupBuilder) {
            StudyGroup group = ((StudyGroup.StudyGroupBuilder) parameter).build();
            return group;
        }
        throw new InvalidInputException("Parameter of add command must be a StudyGroup.StudyGroupBuilder");
    }

    @Override
    public String execute(Object parameter) {
        try {
            group = validate(parameter);
            CollectionManager.commandsList.add("add");
            HistoryParser.parseToFile();

            collectionManager.add(group);
            return "Создание StudyGroup завершено! Его id: " + group.getId() + "\n";
        } catch (InvalidInputException e) {
            return e.getMessage();
        }
    }

}