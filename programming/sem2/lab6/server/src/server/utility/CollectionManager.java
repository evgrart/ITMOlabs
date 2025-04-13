package server.utility;

import general.main_classes.StudyGroup;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Класс для управления коллекцией LinkedList
 */
public class CollectionManager {
    public ZonedDateTime time = ZonedDateTime.now();
    public static ArrayList<String> commandsList = new ArrayList<>();
    public static LinkedList<StudyGroup> groups = new LinkedList<>();
    public static HashMap<Integer, StudyGroup> ids = new HashMap<>();
    public static String FILE_NAME;

    /**
     * Метод удаляет объект из коллекции по его айдишнику
     *
     * @param id объекта, который собираемся удалять
     */
    public void remove_by_id(Integer id) {
        groups.removeIf(group -> group.getId() == id);
        ids.remove(id);
    }

    /**
     * Выводит информацию о коллекции, используется в {@link client.commands.Info}
     */
    public String info() {
        return "Тип: LinkedList\n" + "Дата инициализации: " + time.format(DateTimeFormatter.ISO_ZONED_DATE_TIME) + "\nКоличество элементов: " + groups.size() + "\n";
    }

    /**
     * Принимает на вход созданный объект, который требуется добавить
     * <p></p>
     * Также добавляет пару айди - объект в {@link CollectionManager#ids}
     *
     * @param group этот элемент будет добавлен в коллекцию
     */
    public void add(StudyGroup group) {
        groups.add(group);
        ids.put(group.getId(), group);
    }

    /**
     * Вывести информацию об одном элементе коллекции с помощью {@link StudyGroup#toString()}
     *
     * @param el информация об этом элементе коллекции будет выведена
     */
    public String show(StudyGroup el) {
        return el.toString();
    }

    /**
     * Очищает коллекцию
     */
    public void clear() {
        groups.clear();
        ids.clear();
    }

    /**
     * Удаляет первый элемент коллекции и выводит его
     */
    public String remove_head() {
        if (!groups.isEmpty()) {
            StudyGroup head = groups.get(0);
            Integer id = head.getId();
            remove_by_id(id);
            return this.show(head);
        } else {
            return "Коллекция пуста!";
        }
    }

    /**
     * Заменяет объект коллекции по его id на новый, созданный юзером
     *
     * @param id    элемент с этим id будет изменён
     * @param group старый элемент будет изменён на этот
     */
    public String update(Integer id, StudyGroup group) {
        ids.remove(id);

        int index = -1;
        for (int i = 0; i < groups.size(); i++) {
            StudyGroup el = groups.get(i);
            if (el.getId() == id) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            groups.set(index, group);
            ids.put(group.getId(), group);
            return "Объект под старым id " + id + " был изменён.\nНовый id объекта - " + group.getId() + "\n";
        } else {
            return "Объект с указанным id не найден\n";
        }
    }

}
