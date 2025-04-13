package client.commands;

import client.GroupCreature;
import general.ClientCommand;
import general.Request;
import general.exceptions.InvalidInputException;
import general.interfaces.Initialization;
import general.interfaces.ValidatableCommand;
import general.main_classes.StudyGroup;
import general.interfaces.RequestCreator;

public class Add extends ClientCommand implements Initialization<StudyGroup.StudyGroupBuilder>, ValidatableCommand, RequestCreator {
    public Add(String parameter) {
        super(parameter);
    }

    @Override
    public boolean validate() {
        try {
            if (this.parameter.equals("{element}")) {
                return true;
            } else {
                throw new InvalidInputException("У add должен быть токен {element}!");
            }
        } catch (NumberFormatException | NullPointerException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public Request toRequest() {
        Request<String, StudyGroup.StudyGroupBuilder> request = new Request<>(this.toString(), this.initialize());
        return request;
    }

    @Override
    public StudyGroup.StudyGroupBuilder initialize() {
        System.out.println("Инициализировано создание объекта для добавления его в коллекцию");
        StudyGroup.StudyGroupBuilder group = GroupCreature.createGroup();
        return group;
    }

    @Override
    public String toString() {
        return "add";
    }
}