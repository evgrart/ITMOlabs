package client.commands;

import client.Client;
import client.PersonCreature;
import general.ClientCommand;
import general.Request;
import general.exceptions.InvalidInputException;
import general.interfaces.Initialization;
import general.interfaces.RequestCreator;
import general.interfaces.ValidatableCommand;
import general.main_classes.Person;

public class RemoveAllByGroupAdmin extends ClientCommand implements Initialization<Person>, ValidatableCommand, RequestCreator {
    public RemoveAllByGroupAdmin(String parameter) {
        super(parameter);
    }

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

    public Person initialize() {
        Person admin = PersonCreature.createPerson();
        return admin;
    }

    public Request toRequest() {
        Request<String, Person> request = new Request(this.toString(), this.initialize(), Client.login);
        return request;
    }

    public String toString() {
        return "remove_all_by_group_admin";
    }
}
