//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package server.commands;

import general.exceptions.InvalidInputException;
import server.interfaces.Executable;
import server.interfaces.ValidatableParameter;
import server.utility.HistoryParser;

public class Help extends Command implements ValidatableParameter<Void>, Executable {
    private final String helpMessage = "help : вывести справку по доступным командам\ninfo : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов)\nshow : вывести в стандартный поток вывода все элементы коллекции в строковом представлении\nadd {element} : добавить новый элемент в коллекцию\nupdate id {element} : обновить значение элемента коллекции, id которого равен заданному\nremove_by_id id : удалить элемент из коллекции по его id\nexecute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме\nexit : завершить работу клиента\nremove_head : вывести первый элемент коллекции и удалить его\nremove_lower {element} : удалить из коллекции все элементы, меньшие, чем заданный\nhistory : вывести последние 8 команд (без их аргументов)\nremove_all_by_group_admin groupAdmin : удалить из коллекции все элементы, значение поля groupAdmin которого эквивалентно заданному\ncount_greater_than_group_admin groupAdmin : вывести количество элементов, значение поля groupAdmin которых больше заданного\nfilter_greater_than_form_of_education formOfEducation : вывести элементы, значение поля formOfEducation которых больше заданного\n";
    private String login;

    public Help() {
    }

    public Void validate(Object parameter) {
        if (parameter != null) {
            throw new InvalidInputException("Parameter of help command must be a null");
        } else {
            return null;
        }
    }

    public synchronized String execute(Object parameter, String login) {
        try {
            this.login = login;
            this.validate(parameter);
            HistoryParser.commandsList.add("help");
            HistoryParser.parseToFile();
            return "help : вывести справку по доступным командам\ninfo : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов)\nshow : вывести в стандартный поток вывода все элементы коллекции в строковом представлении\nadd {element} : добавить новый элемент в коллекцию\nupdate id {element} : обновить значение элемента коллекции, id которого равен заданному\nremove_by_id id : удалить элемент из коллекции по его id\nexecute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме\nexit : завершить работу клиента\nremove_head : вывести первый элемент коллекции и удалить его\nremove_lower {element} : удалить из коллекции все элементы, меньшие, чем заданный\nhistory : вывести последние 8 команд (без их аргументов)\nremove_all_by_group_admin groupAdmin : удалить из коллекции все элементы, значение поля groupAdmin которого эквивалентно заданному\ncount_greater_than_group_admin groupAdmin : вывести количество элементов, значение поля groupAdmin которых больше заданного\nfilter_greater_than_form_of_education formOfEducation : вывести элементы, значение поля formOfEducation которых больше заданного\n";
        } catch (InvalidInputException e) {
            return e.getMessage();
        }
    }
}
