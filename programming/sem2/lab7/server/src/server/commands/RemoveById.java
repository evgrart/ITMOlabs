//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package server.commands;

import general.exceptions.InvalidInputException;
import server.interfaces.Executable;
import server.interfaces.ValidatableParameter;
import server.utility.CollectionManager;
import server.utility.HistoryParser;

public class RemoveById extends Command implements ValidatableParameter<Integer>, Executable {
    private String login;

    public RemoveById() {
    }

    public Integer validate(Object parameter) {
        if (parameter instanceof Integer) {
            return (Integer)parameter;
        } else {
            throw new InvalidInputException("Parameter of update command must be a Integer");
        }
    }

    public synchronized String execute(Object parameter, String login) {
        this.login = login;
        Integer id = this.validate(parameter);
        HistoryParser.commandsList.add("remove_by_id");
        HistoryParser.parseToFile();
        if (CollectionManager.groups.isEmpty()) {
            return "Коллекция пуста - удалять нечего\n";
        } else if (!CollectionManager.ids.containsKey(id)) {
            return "Объект по заданному id не найден\n";
        } else {
            int num = this.collectionManager.remove_by_id(id, login);
            if (num == 0) {
                return "Элемент коллекции с id " + id + " был удалён\n";
            } else {
                return num == -1 ? "Ошибка при удалении элемента с БД" : "Вы не можете изменять чужие элементы коллекции\n";
            }
        }
    }
}
