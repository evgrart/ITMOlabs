package client.commands;

import client.GroupCreature;
import general.ClientCommand;
import general.Request;
import general.interfaces.Initialization;
import general.interfaces.RequestCreator;
import general.interfaces.ValidatableCommand;
import general.Tuple;
import general.main_classes.StudyGroup;


/**
 * Обновляет элемент коллекции на заданный юзером
 */
public class Update extends ClientCommand implements Initialization<Tuple<Integer, StudyGroup.StudyGroupBuilder>>, ValidatableCommand, RequestCreator {
    public Update(String parameter) {
        super(parameter);
    }

    @Override
    public boolean validate() {
        try {
            Integer.parseInt(this.parameter);
            return true;
        } catch (NumberFormatException e) {
            if (this.parameter == null) {
                System.out.println("Добавьте id {element}, где id - число");
            } else {
                System.out.println("id должен быть числом! Введите команду ещё раз");
            }

            return false;
        }
    }

    @Override
    public Tuple<Integer, StudyGroup.StudyGroupBuilder> initialize() {
        Integer id = Integer.parseInt(this.parameter);
        System.out.println("Инициализировано создание объекта для добавления его в коллекцию вместо элемента с id: " + id);
        StudyGroup.StudyGroupBuilder group = GroupCreature.createGroup();
        Tuple<Integer, StudyGroup.StudyGroupBuilder> tuple = new Tuple<>(id, group);
        return tuple;
    }

    @Override
    public Request toRequest() {
        Request<String, Tuple<Integer, StudyGroup.StudyGroupBuilder>> request = new Request<>(this.toString(), this.initialize());
        return request;
    }

    @Override
    public String toString() {
        return "update";
    }
}
