package general;

import client.Client;
import general.interfaces.RequestCreator;
import general.interfaces.ValidatableCommand;
import java.io.Serializable;

public class ClientCommand implements ValidatableCommand, Serializable, RequestCreator {
    public String parameter;

    public ClientCommand(String parameter) {
        this.parameter = parameter;
    }

    public boolean validate() {
        return true;
    }

    public Request toRequest() {
        Request<String, String> request = new Request(this.toString(), "Такой команды нет! Используйте команду help, чтобы посмотреть список команд\n", Client.login);
        return request;
    }

    public String toString() {
        return "command";
    }
}
