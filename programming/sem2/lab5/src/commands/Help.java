package commands;

import exceptions.InvalidInputException;
import interfaces.Executable;
import interfaces.ValidatableCommand;
import main_classes.Main;
import utility.HistoryParser;

/**
 * Выводит список доступных команд
 */
public class Help extends Command implements Executable, ValidatableCommand {
    private final String helpMessage = "help : вывести справку по доступным командам\ninfo : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов)\nshow : вывести в стандартный поток вывода все элементы коллекции в строковом представлении\nadd {element} : добавить новый элемент в коллекцию\nupdate id {element} : обновить значение элемента коллекции, id которого равен заданному\nremove_by_id id : удалить элемент из коллекции по его id\nclear : очистить коллекцию\nsave : сохранить коллекцию в файл\nexecute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.\nexit : завершить программу (без сохранения в файл)\nremove_head : вывести первый элемент коллекции и удалить его\nremove_lower {element} : удалить из коллекции все элементы, меньшие, чем заданный\nhistory : вывести последние 8 команд (без их аргументов)\nremove_all_by_group_admin groupAdmin : удалить из коллекции все элементы, значение поля groupAdmin которого эквивалентно заданному\ncount_greater_than_group_admin groupAdmin : вывести количество элементов, значение поля groupAdmin которых больше заданного\nfilter_greater_than_form_of_education formOfEducation : вывести элементы, значение поля formOfEducation которых больше заданного\nsort (инородная команда) : отсортировать коллекцию";

    public Help(Object parameter) {
        super(parameter);
    }

    @Override
    public boolean validate() {
        try {
            if (this.parameter == null) {
                return true;
            } else {
                throw new InvalidInputException("У help не должно быть аргументов!");
            }
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public void execute() {

        Main.commandsList.add("help");
        HistoryParser.parseToFile();
        System.out.println(helpMessage + "\n");

    }
}
