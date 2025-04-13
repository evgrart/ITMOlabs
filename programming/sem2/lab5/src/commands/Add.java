package commands;

import exceptions.InvalidInputException;
import interfaces.Executable;
import interfaces.ValidatableCommand;
import main_classes.Main;
import main_classes.StudyGroup;
import utility.CollectionManager;
import utility.GroupCreature;
import utility.HistoryParser;

/**
 * Добавляет элемент в коллекцию
 * @see CollectionManager#add(StudyGroup)
 */
public class Add extends Command implements Executable, ValidatableCommand {
    public Add(Object parameter) {
        super(parameter);
    }

    @Override
    public boolean validate() {
        try {
            if (this.parameter.equals("{element}")) {
                return true;
            } else {
                throw new InvalidInputException("У add должен быть токен {element}!");
            }
        } catch (NumberFormatException | NullPointerException e) {
            System.out.println("У add должен быть токен {element}!");
            return false;
        }
    }

    @Override
    public void execute() {
        
        Main.commandsList.add("add {element}");
        HistoryParser.parseToFile();
        System.out.println("Инициализировано создание объекта для добавления его в коллекцию");
        StudyGroup result = GroupCreature.createGroup();
        CollectionManager.add(result);
        System.out.println("Создание StudyGroup завершено! Его id: " + result.getId() + "\n");
    }
}