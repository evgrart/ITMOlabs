package client.commands;

import client.Client;
import client.GroupCreature;
import general.ClientCommand;
import general.Request;
import general.exceptions.InvalidInputException;
import general.interfaces.Initialization;
import general.interfaces.RequestCreator;
import general.interfaces.ValidatableCommand;
import general.main_classes.StudyGroup;

public class Add extends ClientCommand implements Initialization<StudyGroup.StudyGroupBuilder>, ValidatableCommand, RequestCreator {
    public Add(String parameter) {
        super(parameter);
    }

    public boolean validate() {
        try {
            if (this.parameter != null && this.parameter.equals("{element}")) {
                return true;
            } else {
                throw new InvalidInputException("У add должен быть токен {element}!");
            }
        } catch (NullPointerException | NumberFormatException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public Request toRequest() {
        Request<String, StudyGroup.StudyGroupBuilder> request = new Request(this.toString(), this.initialize(), Client.login);
        return request;
    }

    public StudyGroup.StudyGroupBuilder initialize() {
        System.out.println("Инициализировано создание объекта для добавления его в коллекцию");
        StudyGroup.StudyGroupBuilder group = GroupCreature.createGroup();
        group.login(Client.login);
        return group;
    }

    public String toString() {
        return "add";
    }
}
