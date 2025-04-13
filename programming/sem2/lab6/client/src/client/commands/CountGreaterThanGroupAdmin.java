package client.commands;

import client.PersonCreature;
import general.ClientCommand;
import general.Request;
import general.exceptions.InvalidInputException;
import general.interfaces.Initialization;
import general.interfaces.RequestCreator;
import general.interfaces.ValidatableCommand;
import general.main_classes.Person;


/**
 * Удаляет те элементы коллекции, у которых значение groupAdmin больше значения у заданной коллекции
 * <p></p>
 * GroupAdmin сортируются по сумме значения UCS каждого символа в строке PassportID
 *
 * @see general.main_classes.Person
 */
public class CountGreaterThanGroupAdmin extends ClientCommand implements Initialization<Person>, ValidatableCommand, RequestCreator {
    public CountGreaterThanGroupAdmin(String parameter) {
        super(parameter);
    }

    @Override
    public boolean validate() {
        try {
            if (this.parameter == null) {
                return true;
            } else {
                throw new InvalidInputException("У count_greater_than_group_admin не должно быть аргументов!\nНачните инициализировать groupAdmin с новой строки");
            }
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public Request toRequest() {
        Request<String, Person> request = new Request<>(this.toString(), this.initialize());
        return request;
    }

    @Override
    public Person initialize() {
        Person person = PersonCreature.createPerson();
        return person;
    }

    @Override
    public String toString() {
        return "count_greater_than_group_admin";
    }
}
