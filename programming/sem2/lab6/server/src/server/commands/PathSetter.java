package server.commands;

import general.exceptions.InvalidInputException;
import server.interfaces.Executable;
import server.interfaces.ValidatableParameter;
import server.utility.CollectionManager;

public class PathSetter extends Command implements ValidatableParameter<String>, Executable {

    @Override
    public String validate(Object parameter) {
        if (parameter instanceof String) {
            throw new InvalidInputException("Parameter of clear command must be a String");
        }
        return (String) parameter;
    }

    @Override
    public String execute(Object parameter) {
        CollectionManager.FILE_NAME = (String) parameter;
        return "";
    }
}
