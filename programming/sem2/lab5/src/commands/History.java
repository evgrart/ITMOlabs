package commands;

import exceptions.InvalidInputException;
import interfaces.Executable;
import interfaces.ValidatableCommand;
import main_classes.Main;
import utility.HistoryParser;

/**
 * Показывает последние 8 введённых юзером команд
 */
public class History extends Command implements Executable, ValidatableCommand {
    int startIndex;

    public History(Object parameter) {
        super(parameter);
        this.startIndex = Math.max(0, Main.commandsList.size() - 8);
    }

    @Override
    public boolean validate() {
        try {
            if (this.parameter == null) {
                return true;
            } else {
                throw new InvalidInputException("У history не должно быть аргументов!");
            }
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public void execute() {
        Main.commandsList.add("history");

        HistoryParser.parseToFile();
        System.out.println();

        for (int i = Main.commandsList.size() - 1; i >= this.startIndex; --i) {
            System.out.println(Main.commandsList.get(i));
        }

        System.out.println();
    }
}
