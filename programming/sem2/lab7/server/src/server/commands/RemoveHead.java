//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package server.commands;

import general.exceptions.InvalidInputException;
import server.interfaces.Executable;
import server.interfaces.ValidatableParameter;
import server.utility.CollectionManager;
import server.utility.HistoryParser;

public class RemoveHead extends Command implements ValidatableParameter<Void>, Executable {
    private String login;

    public RemoveHead() {
    }

    public Void validate(Object parameter) {
        if (parameter != null) {
            throw new InvalidInputException("Parameter of remove_head command must be a null");
        } else {
            return null;
        }
    }

    public synchronized String execute(Object parameter, String login) {
        try {
            this.login = login;
            this.validate(parameter);
            HistoryParser.commandsList.add("remove_head");
            HistoryParser.parseToFile();
            if (CollectionManager.groups.isEmpty()) {
                return "Коллекция пуста - удалять нечего\n";
            } else {
                String var10000 = this.collectionManager.remove_head(login);
                return var10000 + "\n";
            }
        } catch (InvalidInputException e) {
            return e.getMessage();
        }
    }
}
