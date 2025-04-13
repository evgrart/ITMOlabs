package commands;

import exceptions.InvalidInputException;
import interfaces.Executable;
import interfaces.ValidatableCommand;
import main_classes.Main;
import main_classes.StudyGroup;
import utility.CollectionManager;
import utility.HistoryParser;

/**
 * Выводит информацию (поля) каждого элементы коллекции
 *
 * @see CollectionManager#show(StudyGroup)
 */
public class Show extends Command implements Executable, ValidatableCommand {
    public Show(Object parameter) {
        super(parameter);
    }

    @Override
    public boolean validate() {
        if (Main.groups.size() == 0) {
            System.out.println("В коллекции нету ни одного элемента\n");
        }

        try {
            if (this.parameter == null) {
                return true;
            } else {
                throw new InvalidInputException("У show не должно быть аргументов!\n");
            }
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public void execute() {

        Main.commandsList.add("show");
        HistoryParser.parseToFile();

        for (StudyGroup el : Main.groups) {
            System.out.println("Информация об объекте c индексом " + Main.groups.indexOf(el) + " в коллекции");
            CollectionManager.show(el);
            System.out.println();
        }

    }
}
