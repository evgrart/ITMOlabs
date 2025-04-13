package server.commands;

import general.exceptions.InvalidInputException;
import server.interfaces.Executable;
import server.interfaces.ValidatableParameter;
import server.utility.CollectionManager;
import general.main_classes.Person;
import general.main_classes.StudyGroup;
import server.utility.HistoryParser;

import java.util.Iterator;

/**
 * Удаляет все элементы коллекции, у которых GroupAdmin равен инициализированному юзером (сравнение идёт по всем полям)
 *
 * @see Person
 */
public class RemoveAllByGroupAdmin extends Command implements ValidatableParameter<Person>, Executable {
    private boolean flag = true;

    @Override
    public Person validate(Object parameter) {
        if (parameter instanceof Person) {
            return (Person) parameter;
        }
        throw new InvalidInputException("Parameter of remove_all_by_group_admin command must be a Person");
    }

    @Override
    public String execute(Object parameter) {
        Person admin = validate(parameter);
        CollectionManager.commandsList.add("remove_all_by_group_admin");
        HistoryParser.parseToFile();

        if (CollectionManager.groups.isEmpty()) {
            return "Коллекция пуста - удалять нечего\n";
        }

        Iterator<StudyGroup> iterator = CollectionManager.groups.iterator();

        while (iterator.hasNext()) {
            StudyGroup group = iterator.next();
            Person person = group.getGroupAdmin();
            if (person != null && person.equals(admin)) {
                Integer id = group.getId();
                iterator.remove();
                CollectionManager.ids.remove(id);
                flag = false;
            }
        }
        if (flag) {
            return "Совпадений не найдено\n";
        } else {
            return "Все объекты коллекции с равным groupAdmin были удалены!\n";
        }
    }
}
