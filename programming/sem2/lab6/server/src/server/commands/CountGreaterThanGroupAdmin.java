package server.commands;

import general.exceptions.InvalidInputException;
import general.main_classes.Person;
import server.interfaces.Executable;
import server.interfaces.ValidatableParameter;
import server.utility.CollectionManager;
import server.utility.HistoryParser;


/**
 * Удаляет те элементы коллекции, у которых значение groupAdmin больше значения у заданной коллекции
 * <p></p>
 * GroupAdmin сортируются по сумме значения UCS каждого символа в строке PassportID
 *
 * @see general.main_classes.Person
 */
public class CountGreaterThanGroupAdmin extends Command implements ValidatableParameter<Person>, Executable {
    @Override
    public Person validate(Object parameter) {
        if (parameter instanceof Person) {
            return (Person) parameter;
        }
        throw new InvalidInputException("Parameter of count_greater_than_group_admin command must be a Person");
    }

    @Override
    public String execute(Object parameter) {
        Person target = validate(parameter);
        CollectionManager.commandsList.add("count_greater_than_group_admin");
        HistoryParser.parseToFile();

        long count = collectionManager.groups.stream().filter(group -> group.getGroupAdmin() != null && group.getGroupAdmin().compareTo(target) > 0).count();
        return "Выявлено совпадений: " + count + "\n";

    }

}

