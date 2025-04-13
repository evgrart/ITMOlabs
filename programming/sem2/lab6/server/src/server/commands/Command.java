package server.commands;

import general.exceptions.InvalidInputException;
import server.interfaces.Executable;
import server.utility.CollectionManager;


/**
 * Класс-предок для всех команд пакета commands, тут обрабатываются всё, что ожидалось как команда, но среди команд этого нет
 */
public class Command implements Executable {
    CollectionManager collectionManager = new CollectionManager();
    StringBuilder sb = new StringBuilder();

    /**
     * Параметр команды: для update это id, для add - element и тд. Вообще-м обязательная часть синтаксиса какой-то команды
     * <p></p>
     * Если у команды нет параметра, то необходимо отправить в конструктор null
     */
    private String check(Object parameter) {
        if (parameter instanceof String) {
            return (String) parameter;
        }
        throw new InvalidInputException("Parameter of add command must be a StudyGroup.StudyGroupBuilder");
    }

    @Override
    public String execute(Object parameter) {
        return check(parameter);
    }

}
