//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package server.commands;

import general.exceptions.InvalidInputException;
import general.main_classes.FormOfEducation;
import general.main_classes.StudyGroup;
import server.interfaces.Executable;
import server.interfaces.ValidatableParameter;
import server.utility.CollectionManager;
import server.utility.HistoryParser;

public class FilterGreaterThanFormOfEducation extends Command implements ValidatableParameter<FormOfEducation>, Executable {
    private String login;
    FormOfEducation form;
    boolean flag = true;

    public FilterGreaterThanFormOfEducation() {
    }

    public FormOfEducation validate(Object parameter) {
        if (parameter instanceof FormOfEducation) {
            return (FormOfEducation)parameter;
        } else {
            throw new InvalidInputException("Parameter of filter_greater_than_form_of_education command must be a FormOfEducation");
        }
    }

    public synchronized String execute(Object parameter, String login) {
        this.login = login;
        this.form = this.validate(parameter);
        HistoryParser.commandsList.add("filter_greater_than_form_of_education");
        HistoryParser.parseToFile();

        for(StudyGroup group : CollectionManager.groups) {
            FormOfEducation checker = group.getFormOfEducation();
            if (checker != null && checker.ordinal() > this.form.ordinal()) {
                this.sb.append(String.valueOf(group) + "\n");
                this.flag = false;
            }
        }

        if (this.flag) {
            return "В коллекции нет объектов, у которых значение FormOfEducation больше, чем " + this.form.toString() + "\n";
        } else {
            return "Следующие элементы коллекции будут удалены:\n" + this.sb.toString();
        }
    }
}
