package client.commands;

import general.ClientCommand;
import general.Request;
import general.interfaces.Initialization;
import general.interfaces.RequestCreator;
import general.interfaces.ValidatableCommand;


/**
 * Удаляет все элементы коллекции, у которых значение id меньше заданного юзером (сравнение происходит как и у int'ов)
 *
 */
public class RemoveLower extends ClientCommand implements Initialization<Integer>, ValidatableCommand, RequestCreator {
    public RemoveLower(String parameter) {
        super(parameter);
    }

    @Override
    public boolean validate() {
        try {
            Integer.parseInt(this.parameter);
            return true;
        } catch (NumberFormatException e) {
            if (this.parameter == null) {
                System.out.println("Добавьте id, где id - число");
            } else {
                System.out.println("id должен быть целым числом (от -2^31 до 2^31 - 1)! Введите команду ещё раз");
            }
            return false;
        }
    }

    @Override
    public Integer initialize() {
        Integer id = Integer.parseInt(this.parameter);
        return id;

    }

    @Override
    public Request toRequest() {
        Request<String, Integer> request = new Request<>(this.toString(), this.initialize());
        return request;
    }

    @Override
    public String toString() {
        return "remove_lower";
    }
}
