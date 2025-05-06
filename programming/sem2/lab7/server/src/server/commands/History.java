//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package server.commands;

import general.exceptions.InvalidInputException;
import server.interfaces.Executable;
import server.interfaces.ValidatableParameter;
import server.utility.HistoryParser;

public class History extends Command implements ValidatableParameter<Void>, Executable {
    private int startIndex;
    private String login;

    public History() {
    }

    public Void validate(Object parameter) {
        if (parameter != null) {
            throw new InvalidInputException("Parameter of history command must be a null");
        } else {
            return null;
        }
    }

    public synchronized String execute(Object parameter, String login) {
        try {
            this.login = login;
            this.startIndex = Math.max(0, HistoryParser.commandsList.size() - 7);
            this.sb.setLength(0);
            this.validate(parameter);
            HistoryParser.commandsList.add("history");
            HistoryParser.parseToFile();
            this.sb.append("\n");

            for(int i = HistoryParser.commandsList.size() - 1; i >= this.startIndex; --i) {
                this.sb.append((String)HistoryParser.commandsList.get(i) + "\n");
            }

            return this.sb.toString();
        } catch (InvalidInputException e) {
            return e.getMessage();
        }
    }
}
