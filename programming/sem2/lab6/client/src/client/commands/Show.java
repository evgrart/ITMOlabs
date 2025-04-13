package client.commands;

import general.ClientCommand;
import general.Request;
import general.exceptions.InvalidInputException;
import general.interfaces.RequestCreator;
import general.interfaces.ValidatableCommand;

/**
 * Выводит информацию (поля) каждого элементы коллекции
 *
 */
public class Show extends ClientCommand implements ValidatableCommand, RequestCreator {
    public Show(String parameter) {
        super(parameter);
    }

    @Override
    public boolean validate() {

        try {
            if (this.parameter == null) {
                return true;
            } else {
                throw new InvalidInputException("У show не должно быть аргументов!\n");
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
        return "show";
    }
}
