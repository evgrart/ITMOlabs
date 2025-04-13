package commands;

import exceptions.InvalidInputException;
import interfaces.Executable;
import interfaces.ValidatableCommand;
import main_classes.Main;
import utility.HistoryParser;

/**
 * Завершает сессию, работает засчёт закрытия всех потоков
 */
public class Exit extends Command implements Executable, ValidatableCommand {
    public Exit(Object parameter) {
        super(parameter);
    }

    @Override
    public boolean validate() {
        try {
            if (this.parameter == null) {
                return true;
            } else {
                throw new InvalidInputException("У exit не должно быть аргументов!");
            }
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public void execute() {

        Main.commandsList.add("exit");
        HistoryParser.parseToFile();
        System.out.println("Программа завершена");
        System.exit(0);
    }
}
