//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package server.commands;

import general.exceptions.InvalidInputException;

import java.sql.SQLException;

import general.main_classes.StudyGroup;
import server.interfaces.Executable;
import server.interfaces.ValidatableParameter;
import server.utility.HistoryParser;

public class Add extends Command implements ValidatableParameter<StudyGroup>, Executable {
    private StudyGroup group;
    private String login;

    public Add() {
    }

    public StudyGroup validate(Object parameter) {
        if (parameter instanceof StudyGroup.StudyGroupBuilder) {
            StudyGroup group = ((StudyGroup.StudyGroupBuilder)parameter).build();
            return group;
        } else {
            throw new InvalidInputException("Parameter of add command must be a StudyGroup.StudyGroupBuilder");
        }
    }

    public synchronized String execute(Object parameter, String login) {
        try {
            this.login = login;
            this.group = this.validate(parameter);
            HistoryParser.commandsList.add("add");
            HistoryParser.parseToFile();
            return this.collectionManager.add(this.group, login);
        } catch (SQLException | InvalidInputException e) {
            return e.getMessage();
        }
    }
}
