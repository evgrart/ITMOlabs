package server.commands;

import general.exceptions.InvalidInputException;
import server.interfaces.Executable;
import server.interfaces.ValidatableParameter;
import general.main_classes.FormOfEducation;
import general.main_classes.StudyGroup;
import server.utility.CollectionManager;
import server.utility.HistoryParser;


/**
 * Выводит все элементы коллекции, у которых значение FormOfEducation больше заданного. Элементы сравниваются по их порядку в {@link FormOfEducation}
 *
 * @see CollectionManager#show(StudyGroup)
 */
public class FilterGreaterThanFormOfEducation extends Command implements ValidatableParameter<FormOfEducation>, Executable {

    FormOfEducation form;
    boolean flag = true;

    @Override
    public FormOfEducation validate(Object parameter) {
        if (parameter instanceof FormOfEducation) {
            return (FormOfEducation) parameter;
        }
        throw new InvalidInputException("Parameter of filter_greater_than_form_of_education command must be a FormOfEducation");
    }

    @Override
    public String execute(Object parameter) {
        form = validate(parameter);
        CollectionManager.commandsList.add("filter_greater_than_form_of_education");
        HistoryParser.parseToFile();

        for (StudyGroup group : CollectionManager.groups) {
            FormOfEducation checker = group.getFormOfEducation();
            if (checker == null) {
                continue;
            }
            if (checker.ordinal() > form.ordinal()) {
                sb.append(group + "\n");
                flag = false;
            }
        }
        if (flag) {
            return "В коллекции нет объектов, у которых значение FormOfEducation больше, чем " + form.toString() + "\n";
        }
        return "Следующие элементы коллекции будут удалены:\n" + sb.toString();
    }
}
