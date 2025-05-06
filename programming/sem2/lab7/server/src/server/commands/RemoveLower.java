//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package server.commands;

import general.exceptions.InvalidInputException;

import java.sql.SQLException;
import java.util.Iterator;

import general.main_classes.StudyGroup;
import server.interfaces.Executable;
import server.interfaces.ValidatableParameter;
import server.utility.CollectionManager;
import server.utility.HistoryParser;
import server.utility.RequestRunner;

public class RemoveLower extends Command implements ValidatableParameter<Integer>, Executable {
    private String login;

    public RemoveLower() {
    }

    public Integer validate(Object parameter) {
        if (parameter instanceof Integer) {
            return (Integer)parameter;
        } else {
            throw new InvalidInputException("Parameter of remove_lower command must be a Integer");
        }
    }

    public synchronized String execute(Object parameter, String login) {
        this.login = login;
        Integer id = this.validate(parameter);
        HistoryParser.commandsList.add("remove_lower");
        HistoryParser.parseToFile();
        if (CollectionManager.groups.isEmpty()) {
            return "Коллекция пуста - удалять нечего\n";
        } else {
            boolean isRemoved = false;
            Iterator<StudyGroup> iterator = CollectionManager.groups.iterator();

            while(iterator.hasNext()) {
                StudyGroup el = iterator.next();
                if (el.getId() < id && el.getLogin().equals(login)) {
                    this.sb.append("Элемент с id " + el.getId() + " был удалён\n");

                    try {
                        RequestRunner.dbmanager.deleteGroup(el.getId(), login);
                        iterator.remove();
                        CollectionManager.ids.remove(id);
                        isRemoved = true;
                    } catch (SQLException e) {
                    }
                }
            }

            if (!isRemoved) {
                return "Элементов с меньшим id нет\n";
            } else {
                return this.sb.toString() + "\n";
            }
        }
    }
}
