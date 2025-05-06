package client.commands;

import client.Client;
import client.GroupCreature;
import general.ClientCommand;
import general.Request;
import general.Tuple;
import general.interfaces.Initialization;
import general.interfaces.RequestCreator;
import general.interfaces.ValidatableCommand;
import general.main_classes.StudyGroup;

public class Update extends ClientCommand implements Initialization<Tuple<Integer, StudyGroup.StudyGroupBuilder>>, ValidatableCommand, RequestCreator {
    public Update(String parameter) {
        super(parameter);
    }

    public boolean validate() {
        try {
            Integer.parseInt(this.parameter);
            return true;
        } catch (NumberFormatException var2) {
            if (this.parameter == null) {
                System.out.println("Добавьте id {element}, где id - число");
            } else {
                System.out.println("id должен быть числом! Введите команду ещё раз");
            }

            return false;
        }
    }

    public Tuple<Integer, StudyGroup.StudyGroupBuilder> initialize() {
        Integer id = Integer.parseInt(this.parameter);
        System.out.println("Инициализировано создание объекта для добавления его в коллекцию вместо элемента с id: " + id);
        StudyGroup.StudyGroupBuilder group = GroupCreature.createGroup();
        Tuple<Integer, StudyGroup.StudyGroupBuilder> tuple = new Tuple(id, group);
        return tuple;
    }

    public Request toRequest() {
        Request<String, Tuple<Integer, StudyGroup.StudyGroupBuilder>> request = new Request(this.toString(), this.initialize(), Client.login);
        return request;
    }

    public String toString() {
        return "update";
    }
}
