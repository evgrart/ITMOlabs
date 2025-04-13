package commands;

import interfaces.Executable;
import interfaces.ValidatableCommand;
import main_classes.Main;
import utility.CollectionManager;

/**
 * Удаляет элемент коллекции по его id
 *
 * @see CollectionManager#remove_by_id(Integer)
 * @see Main#ids
 */
public class RemoveById extends Command implements Executable, ValidatableCommand {
    public RemoveById(Object parameter) {
        super(parameter);
    }

    @Override
    public boolean validate() {
        try {
            Integer.parseInt((String) this.parameter);
            return true;
        } catch (NumberFormatException e) {
            if (this.parameter == null) {
                System.out.println("Добавьте id, где id - число");
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
            Main.commandsList.add("remove_by_id");
            CollectionManager.remove_by_id(id);
            System.out.println("Элемент с id " + id + " был удалён\n");
        }

    }
}
