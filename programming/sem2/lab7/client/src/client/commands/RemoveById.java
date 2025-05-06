package client.commands;

import client.Client;
import general.ClientCommand;
import general.Request;
import general.interfaces.Initialization;
import general.interfaces.RequestCreator;
import general.interfaces.ValidatableCommand;

public class RemoveById extends ClientCommand implements Initialization<Integer>, ValidatableCommand, RequestCreator {
    public RemoveById(String parameter) {
        super(parameter);
    }

    public boolean validate() {
        try {
            Integer.parseInt(this.parameter);
            return true;
        } catch (NumberFormatException var2) {
            if (this.parameter == null) {
                System.out.println("Добавьте id, где id - число");
            } else {
                System.out.println("id должен быть числом! Введите команду ещё раз");
            }

            return false;
        }
    }

    public Request toRequest() {
        Request<String, Integer> request = new Request(this.toString(), this.initialize(), Client.login);
        return request;
    }

    public Integer initialize() {
        Integer id = Integer.parseInt(this.parameter);
        return id;
    }

    public String toString() {
        return "remove_by_id";
    }
}
