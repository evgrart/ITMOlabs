//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package server.commands;

import general.Tuple;
import general.exceptions.InvalidInputException;
import general.main_classes.StudyGroup;
import server.interfaces.Executable;
import server.interfaces.ValidatableParameter;
import server.utility.HistoryParser;

public class Update extends Command implements ValidatableParameter<Tuple>, Executable {
    private String login;

    public Update() {
    }

    public Tuple<Integer, StudyGroup.StudyGroupBuilder> validate(Object parameter) {
        if (parameter instanceof Tuple) {
            if (((Tuple)parameter).getFirst() instanceof Integer && ((Tuple)parameter).getSecond() instanceof StudyGroup.StudyGroupBuilder) {
            }

            return (Tuple)parameter;
        } else {
            throw new InvalidInputException("Parameter of update command must be a Integer");
        }
    }

    public synchronized String execute(Object parameter, String login) {
        this.login = login;
        Tuple<Integer, StudyGroup.StudyGroupBuilder> tuple = this.validate(parameter);
        HistoryParser.commandsList.add("update");
        HistoryParser.parseToFile();
        Integer id = tuple.getFirst();
        StudyGroup group = (tuple.getSecond()).build();
        return this.collectionManager.update(id, group, login);
    }
}
