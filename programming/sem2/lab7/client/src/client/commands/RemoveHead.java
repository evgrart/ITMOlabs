package client.commands;

import client.Client;
import general.ClientCommand;
import general.Request;
import general.exceptions.InvalidInputException;
import general.interfaces.RequestCreator;
import general.interfaces.ValidatableCommand;

public class RemoveHead extends ClientCommand implements ValidatableCommand, RequestCreator {
    public RemoveHead(String parameter) {
        super(parameter);
    }

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

    public Request toRequest() {
        Request<String, Void> request = new Request(this.toString(), null, Client.login);
        return request;
    }

    public String toString() {
        return "remove_head";
    }
}
