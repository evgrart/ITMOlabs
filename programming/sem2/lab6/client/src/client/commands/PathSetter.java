package client.commands;


import general.interfaces.Initialization;
import general.interfaces.RequestCreator;
import general.interfaces.ValidatableCommand;
import general.ClientCommand;
import general.Request;
import general.exceptions.InvalidInputException;


public class PathSetter extends ClientCommand implements Initialization<String>, ValidatableCommand, RequestCreator {
    private String path;
    public PathSetter(String parameter, String path) {
        super(parameter);
        this.path = path;
    }

    @Override
    public Request toRequest() {
        Request<String, String> request = new Request<>(this.toString(), this.initialize());
        return request;
    }

    @Override
    public String initialize() {
        return path;
    }

    @Override
    public String toString() {
        return "path_setter";
    }
}
