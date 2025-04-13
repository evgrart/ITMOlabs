package client.commands;

import general.ClientCommand;
import general.Request;
import general.interfaces.RequestCreator;
import general.interfaces.ValidatableCommand;
import general.main_classes.FormOfEducation;


/**
 * Выводит все элементы коллекции, у которых значение FormOfEducation больше заданного. Элементы сравниваются по их порядку в {@link FormOfEducation}
 *
 */
public class FilterGreaterThanFormOfEducation extends ClientCommand implements ValidatableCommand, RequestCreator {
    public FilterGreaterThanFormOfEducation(String parameter) {
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

    @Override
    public Request toRequest() {
        Request<String, FormOfEducation> request = new Request<>(this.toString(), form);
        return request;
    }

    @Override
    public String toString() {
        return "filter_greater_than_form_of_education";
    }
}
