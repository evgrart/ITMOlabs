//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package server.commands;

import general.exceptions.InvalidInputException;

import java.sql.SQLException;
import java.util.Iterator;

import general.main_classes.Person;
import general.main_classes.StudyGroup;
import server.interfaces.Executable;
import server.interfaces.ValidatableParameter;
import server.utility.CollectionManager;
import server.utility.HistoryParser;
import server.utility.RequestRunner;

public class RemoveAllByGroupAdmin extends Command implements ValidatableParameter<Person>, Executable {
    private boolean flag = true;
    private String login;

    public RemoveAllByGroupAdmin() {
    }

    public Person validate(Object parameter) {
        if (parameter instanceof Person) {
            return (Person)parameter;
        } else {
            throw new InvalidInputException("Parameter of remove_all_by_group_admin command must be a Person");
        }
    }

    public synchronized String execute(Object parameter, String login) {
        this.login = login;
        Person admin = this.validate(parameter);
        HistoryParser.commandsList.add("remove_all_by_group_admin");
        HistoryParser.parseToFile();
        if (CollectionManager.groups.isEmpty()) {
            return "Коллекция пуста - удалять нечего\n";
        } else {
            Iterator<StudyGroup> iterator = CollectionManager.groups.iterator();

            while(iterator.hasNext()) {
                StudyGroup group = iterator.next();
                Person person = group.getGroupAdmin();
                if (person != null && person.equals(admin) && group.getLogin().equals(login)) {
                    Integer id = group.getId();

                    try {
                        RequestRunner.dbmanager.deleteGroup(id, login);
                        iterator.remove();
                        CollectionManager.ids.remove(id);
                        this.flag = false;
                    } catch (SQLException var9) {
                    }
                }
            }

            if (this.flag) {
                return "Совпадений не найдено\n";
            } else {
                return "Все объекты коллекции с равным groupAdmin были удалены!\n";
            }
        }
    }
}
