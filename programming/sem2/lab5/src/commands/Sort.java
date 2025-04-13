package commands;

import exceptions.InvalidInputException;
import interfaces.Executable;
import interfaces.ValidatableCommand;
import main_classes.Main;
import utility.HistoryParser;

import java.util.Collections;

/**
 * Сортирует элементы коллекции по ключу - id (этой команды нет в ТЗ)
 */
public class Sort extends Command implements Executable, ValidatableCommand {
    public Sort(Object parameter) {
        super(parameter);
    }

    @Override
    public boolean validate() {
        try {
            if (this.parameter == null) {
                return true;
            } else {
                throw new InvalidInputException("У sort не должно быть аргументов!");
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
        Collections.sort(Main.groups);
        System.out.println("Коллекция была отсортирована по значениям id\n");
    }
}
