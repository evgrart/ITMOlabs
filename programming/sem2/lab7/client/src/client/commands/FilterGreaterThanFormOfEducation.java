package client.commands;

import client.Client;
import general.ClientCommand;
import general.Request;
import general.interfaces.RequestCreator;
import general.interfaces.ValidatableCommand;
import general.main_classes.FormOfEducation;

public class FilterGreaterThanFormOfEducation extends ClientCommand implements ValidatableCommand, RequestCreator {
    FormOfEducation form;

    public FilterGreaterThanFormOfEducation(String parameter) {
        super(parameter);
    }

    public boolean validate() {
        try {
            this.form = FormOfEducation.valueOf(String.valueOf(this.parameter));
            return true;
        } catch (IllegalArgumentException var2) {
            if (this.parameter == null) {
                System.out.println("Добавьте FormOfEducation из списка: DISTANCE_EDUCATION, FULL_TIME_EDUCATION, EVENING_CLASSES");
            } else {
                System.out.println("FormOfEducation должно быть из перечня: DISTANCE_EDUCATION, FULL_TIME_EDUCATION, EVENING_CLASSES!");
            }

            return false;
        }
    }

    public Request toRequest() {
        Request<String, FormOfEducation> request = new Request(this.toString(), this.form, Client.login);
        return request;
    }

    public String toString() {
        return "filter_greater_than_form_of_education";
    }
}
