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
 * Удаляет все элементы коллекции, у которых GroupAdmin равен инициализированному юзером (сравнение идёт по всем полям)
 *
 * @see Person
 */
public class RemoveAllByGroupAdmin extends ClientCommand implements Initialization<Person>, ValidatableCommand, RequestCreator {
    public RemoveAllByGroupAdmin(String parameter) {
        super(parameter);
    }

    @Override
    public boolean validate() {
        try {
            if (this.parameter == null) {
                return true;
            } else {
                throw new InvalidInputException("У remove_all_by_group_admin не должно быть аргументов!\nНачните инициализировать groupAdmin с новой строки");
            }
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public Person initialize() {
        Person admin = PersonCreature.createPerson();
        return admin;
    }

    @Override
    public Request toRequest() {
        Request<String, Person> request = new Request<>(this.toString(), this.initialize());
        return request;
    }

    @Override
    public String toString() {
        return "remove_all_by_group_admin";
    }
}
