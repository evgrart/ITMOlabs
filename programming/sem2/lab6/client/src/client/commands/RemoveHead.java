
package client.commands;

import general.ClientCommand;
import general.Request;
import general.exceptions.InvalidInputException;
import general.interfaces.RequestCreator;
import general.interfaces.ValidatableCommand;


/**
 * Выводит и удаляет (аналог .pop) первый элемент коллекции (с индексом 0)
 *
 */
public class RemoveHead extends ClientCommand implements ValidatableCommand, RequestCreator {
    public RemoveHead(String parameter) {
        super(parameter);
    }

    @Override
    public boolean validate() {
        try {
            if (this.parameter == null) {
                return true;
            } else {
                throw new InvalidInputException("У remove_head не должно быть аргументов!");
            }
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            return false;
        }

    }

    @Override
    public Request toRequest() {
        Request<String, Void> request = new Request<>(this.toString(), null);
        return request;
    }

    @Override
    public String toString() {
        return "remove_head";
    }
}
