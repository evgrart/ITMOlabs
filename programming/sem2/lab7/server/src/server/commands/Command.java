//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package server.commands;

import general.exceptions.InvalidInputException;
import server.interfaces.Executable;
import server.utility.CollectionManager;

public class Command implements Executable {
    CollectionManager collectionManager = new CollectionManager();
    StringBuilder sb = new StringBuilder();

    public Command() {
    }

    private String check(Object parameter) {
        if (parameter instanceof String) {
            return (String)parameter;
        } else {
            throw new InvalidInputException("Parameter of add command must be a StudyGroup.StudyGroupBuilder");
        }
    }

    public String execute(Object parameter, String login) {
        return this.check(parameter);
    }
}
