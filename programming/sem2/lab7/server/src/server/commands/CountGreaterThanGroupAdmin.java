//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package server.commands;

import general.exceptions.InvalidInputException;
import general.main_classes.Person;
import server.interfaces.Executable;
import server.interfaces.ValidatableParameter;
import server.utility.CollectionManager;
import server.utility.HistoryParser;

public class CountGreaterThanGroupAdmin extends Command implements ValidatableParameter<Person>, Executable {
    private String login;

    public CountGreaterThanGroupAdmin() {
    }

    public Person validate(Object parameter) {
        if (parameter instanceof Person) {
            return (Person)parameter;
        } else {
            throw new InvalidInputException("Parameter of count_greater_than_group_admin command must be a Person");
        }
    }

    public synchronized String execute(Object parameter, String login) {
        this.login = login;
        Person target = this.validate(parameter);
        HistoryParser.commandsList.add("count_greater_than_group_admin");
        HistoryParser.parseToFile();
        CollectionManager var10000 = this.collectionManager;
        long count = CollectionManager.groups.stream().filter((group) -> group.getGroupAdmin() != null && group.getGroupAdmin().compareTo(target) > 0).count();
        return "Выявлено совпадений: " + count + "\n";
    }
}
