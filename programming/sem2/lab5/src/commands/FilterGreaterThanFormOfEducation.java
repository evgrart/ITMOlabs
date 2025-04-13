package commands;

import interfaces.Executable;
import interfaces.ValidatableCommand;
import main_classes.FormOfEducation;
import main_classes.Main;
import main_classes.StudyGroup;
import utility.CollectionManager;
import utility.HistoryParser;

/**
 * Выводит все элементы коллекции, у которых значение FormOfEducation больше заданного. Элементы сравниваются по их порядку в {@link FormOfEducation}
 *
 * @see CollectionManager#show(StudyGroup)
 */
public class FilterGreaterThanFormOfEducation extends Command implements Executable, ValidatableCommand {
    public FilterGreaterThanFormOfEducation(Object parameter) {
        super(parameter);
    }

    @Override
    public boolean validate() {
        try {
            form = FormOfEducation.valueOf(String.valueOf(parameter));
            return true;
        } catch (IllegalArgumentException e) {
            if (this.parameter == null) {
                System.out.println("Добавьте FormOfEducation из списка: DISTANCE_EDUCATION, FULL_TIME_EDUCATION, EVENING_CLASSES");
            } else {
                System.out.println("FormOfEducation должно быть из перечня: DISTANCE_EDUCATION, FULL_TIME_EDUCATION, EVENING_CLASSES!");
            }
            return false;
        }
    }

    FormOfEducation form;
    boolean flag = true;

    @Override
    public void execute() {

        Main.commandsList.add("filter_greater_than_form_of_education");
        HistoryParser.parseToFile();
        for (StudyGroup group : Main.groups) {
            FormOfEducation checker = group.getFormOfEducation();
            if (checker.ordinal() > form.ordinal()) {
                CollectionManager.show(group);
                flag = false;
            }
        }
        if (flag) {
            System.out.println("В коллекции нет объектов, у которых значение FormOfEducation больше, чем " + form);
        }
        System.out.println();
    }
}
