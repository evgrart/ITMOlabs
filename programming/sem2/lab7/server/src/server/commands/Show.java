//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package server.commands;

import general.exceptions.InvalidInputException;
import general.main_classes.StudyGroup;
import server.interfaces.Executable;
import server.interfaces.ValidatableParameter;
import server.utility.CollectionManager;
import server.utility.HistoryParser;

public class Show extends Command implements ValidatableParameter<Void>, Executable {
    private String login;

    public Show() {
    }

    public Void validate(Object parameter) {
        if (parameter != null) {
            throw new InvalidInputException("Parameter of show command must be a null");
        } else {
            return null;
        }
    }

    public synchronized String execute(Object parameter, String login) {
        try {
            this.login = login;
            this.validate(parameter);
            HistoryParser.commandsList.add("show");
            HistoryParser.parseToFile();
            this.sb.setLength(0);
            if (CollectionManager.groups.isEmpty()) {
                return "В коллекции нету ни одного элемента\n";
            } else {
                CollectionManager var10000 = this.collectionManager;

                for(StudyGroup el : CollectionManager.groups) {
                    CollectionManager var10001 = this.collectionManager;
                    this.sb.append("Информация об объекте c индексом " + CollectionManager.groups.indexOf(el) + " в коллекции\n");
                    StringBuilder var6 = this.sb;
                    String var7 = this.collectionManager.show(el);
                    var6.append(var7 + "\n\n");
                }

                return this.sb.toString();
            }
        } catch (InvalidInputException e) {
            return e.getMessage();
        }
    }
}
