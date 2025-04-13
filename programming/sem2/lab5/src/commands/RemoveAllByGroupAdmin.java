package commands;

import exceptions.InvalidInputException;
import interfaces.Executable;
import interfaces.ValidatableCommand;
import main_classes.Main;
import main_classes.Person;
import main_classes.StudyGroup;
import utility.CollectionManager;
import utility.HistoryParser;
import utility.PersonCreature;

/**
 * Удаляет все элементы коллекции, у которых GroupAdmin равен инициализированному юзером (сравнение идёт по всем полям)
 *
 * @see Person
 * @see PersonCreature
 */
public class RemoveAllByGroupAdmin extends Command implements Executable, ValidatableCommand {
    public RemoveAllByGroupAdmin(Object parameter) {
        super(parameter);
    }

    @Override
    public boolean validate() {
        try {
            if (this.parameter == null) {
                return true;
            } else {
                throw new InvalidInputException("У remove_all_by_group_admin не должно быть аргументов!\nНачните инициализировать groupAdmin с новой строки");
            }
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public void execute() {

        boolean flag = true;
        Main.commandsList.add("remove_all_by_group_admin");
        HistoryParser.parseToFile();
        Person admin = PersonCreature.createPerson();
        for (StudyGroup group : Main.groups) {
            Person person = group.getGroupAdmin();
            if (person.equals(admin)) {
                Integer id = group.getId();
                CollectionManager.remove_by_id(id);
                flag = false;
            }
        }
        if (flag) {
            System.out.println("Совпадений не найдено\n");
        } else {
            System.out.println("Все объекты коллекции с равным groupAdmin были удалены!\n");
        }
    }
}
