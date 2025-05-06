//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package server.commands;

import general.exceptions.InvalidInputException;
import server.interfaces.Executable;
import server.interfaces.ValidatableParameter;
import server.utility.HistoryParser;

public class Info extends Command implements ValidatableParameter<Void>, Executable {
    private String login;

    public Info() {
    }

    public Void validate(Object parameter) {
        if (parameter != null) {
            throw new InvalidInputException("Parameter of info command must be a null");
        } else {
            return null;
        }
    }

    public synchronized String execute(Object parameter, String login) {
        try {
            this.login = login;
            this.validate(parameter);
            HistoryParser.commandsList.add("info");
            HistoryParser.parseToFile();
            return this.collectionManager.info();
        } catch (InvalidInputException e) {
            return e.getMessage();
        }
    }
}
