//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package server.commands;

import general.exceptions.InvalidInputException;
import server.interfaces.Executable;
import server.interfaces.ValidatableParameter;
import server.utility.HistoryParser;

public class Clear extends Command implements ValidatableParameter<Void>, Executable {
    private String login;

    public Clear() {
    }

    public Void validate(Object parameter) {
        if (parameter != null) {
            throw new InvalidInputException("Parameter of clear command must be a null");
        } else {
            return null;
        }
    }

    public synchronized String execute(Object parameter, String login) {
        try {
            this.login = login;
            this.validate(parameter);
            HistoryParser.commandsList.add("clear");
            HistoryParser.parseToFile();
            this.collectionManager.clear(login);
            return "Collection is cleared\n";
        } catch (InvalidInputException e) {
            return e.getMessage();
        }
    }
}
