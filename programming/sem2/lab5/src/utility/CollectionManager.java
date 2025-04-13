package utility;

import main_classes.Main;
import main_classes.StudyGroup;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Класс для управления коллекцией LinkedList
 */
public class CollectionManager {
    private static LinkedList<StudyGroup> list = Main.groups;

    /**
     * Метод удаляет объект из коллекции по его айдишнику
     *
     * @param id объекта, который собираемся удалять
     */
    public static void remove_by_id(Integer id) {
        list.removeIf(group -> group.getId() == id);
        Main.ids.remove(id);
    }

    /**
     * Выводит информацию о коллекции, используется в {@link commands.Info}
     */
    public static void info() {
        System.out.println("Тип: LinkedList");
        System.out.println("Дата инициализации: " + Main.time.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
        System.out.println("Количество элементов: " + list.size() + "\n");
    }

    /**
     * Принимает на вход созданный объект, который требуется добавить
     * <p></p>
     * Также добавляет пару айди - объект в {@link Main#ids}
     *
     * @param group этот элемент будет добавлен в коллекцию
     */
    public static void add(StudyGroup group) {
        Main.groups.add(group);
        Main.ids.put(group.getId(), group);
    }

    /**
     * Вывести информацию об одном элементе коллекции с помощью {@link StudyGroup#toString()}
     *
     * @param el информация об этом элементе коллекции будет выведена
     */
    public static void show(StudyGroup el) {
        System.out.println(el);
    }

    /**
     * Очищает коллекцию
     */
    public static void clear() {
        Main.groups.clear();
        Main.ids.clear();
    }

    /**
     * Удаляет первый элемент коллекции и выводит его
     */
    public static void remove_head() {
        if (!Main.groups.isEmpty()) {
            StudyGroup head = Main.groups.get(0);
            Integer id = head.getId();
            show(head);
            remove_by_id(id);
        } else {
            System.out.println("Коллекция пуста!");
        }
    }

    /**
     * Заменяет объект коллекции по его id на новый, созданный юзером
     *
     * @param id элемент с этим id будет изменён
     */
    public static void update(Integer id) {
        StudyGroup group = GroupCreature.createGroup();
        Main.ids.remove(id);

        int index = -1;
        for (int i = 0; i < Main.groups.size(); i++) {
            StudyGroup el = Main.groups.get(i);
            if (el.getId() == id) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            Main.groups.set(index, group);
            Main.ids.put(group.getId(), group);
            System.out.println("Объект под старым id " + id + " был изменён.\nНовый id объекта - " + group.getId() + "\n");
        } else {
            System.out.println("Объект с указанным id не найден.");
        }
    }

    /**
     * Удаляет все элементы коллекции с меньшим значением id (id сравниваются также, как и int)
     *
     * @param id все элементы с id меньше заданного будут удалены
     */
    public static void remove_lower(Integer id) {
        if (!Main.ids.containsKey(id)) {
            System.out.println("Объект по заданному id не найден\n");
        } else {
            List<StudyGroup> toRemove = new ArrayList<>();
            boolean isRemoved = false;

            for (StudyGroup el : list) {
                if (el.getId() < id) {
                    System.out.println("Элемент с id " + el.getId() + " был удалён");
                    toRemove.add(el);
                    Main.ids.remove(el.getId());
                    isRemoved = true;
                }
            }

            if (!isRemoved) {
                System.out.println("Введённый элемент является минимальным в коллекции");
            }

            Main.groups.removeAll(toRemove);
            System.out.println();
        }
    }
}
