package commands;

import interfaces.Executable;
import interfaces.ValidatableCommand;
import main_classes.Main;
import utility.CollectionManager;
import utility.HistoryParser;

/**
 * Обновляет элемент коллекции на заданный юзером
 *
 * @see CollectionManager#update(Integer)
 */
public class Update extends Command implements Executable, ValidatableCommand {
    public Update(Object parameter) {
        super(parameter);
    }

    @Override
    public boolean validate() {
        try {
            Integer.parseInt((String) this.parameter);
            return true;
        } catch (NumberFormatException e) {
            if (this.parameter == null) {
                System.out.println("Добавьте id {element}, где id - число");
            } else {
                System.out.println("id должен быть числом! Введите команду ещё раз");
            }

            return false;
        }
    }

    @Override
    public void execute() {

        Integer id = Integer.parseInt((String) this.parameter);
        if (!Main.ids.containsKey(id)) {
            System.out.println("Объект по заданному id не найден\n");
        } else {
            Main.commandsList.add("update");
            HistoryParser.parseToFile();
            CollectionManager.update(id);
        }

    }
}
