//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package server.utility;

import general.main_classes.StudyGroup;

import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CollectionManager {
    public ZonedDateTime time = ZonedDateTime.now();
    public static List<StudyGroup> groups = Collections.synchronizedList(new LinkedList());
    public static HashMap<Integer, StudyGroup> ids = new HashMap();
    public static String FILE_NAME = "legacy";

    public CollectionManager() {
    }

    public synchronized int remove_by_id(Integer id, String login) {
        try {
            StudyGroup group = ids.get(id);
            int num = RequestRunner.dbmanager.deleteGroup(id, login);
            if (num == -1) {
                MyLogger.info("Unsuccessfully remove element from DB");
                return -1;
            } else if (num == -11) {
                MyLogger.info("Client has not roots");
                return -11;
            } else {
                MyLogger.info("Successfully remove element from DB");
                groups.remove(group);
                ids.remove(id);
                return 0;
            }
        } catch (SQLException e) {
            MyLogger.info(e.getMessage());
            return -1;
        }
    }

    public synchronized String info() {
        String var10000 = this.time.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
        return "Тип: LinkedList\nДата инициализации: " + var10000 + "\nКоличество элементов: " + groups.size() + "\n";
    }

    public synchronized String add(StudyGroup group, String login) throws SQLException {
        Integer id = RequestRunner.dbmanager.addStudyGroup(group, login);
        if (id == -1) {
            MyLogger.info("Unsuccessfully add element in DB");
            return "Ошибка при добавлении в БД \n";
        } else {
            StudyGroup.StudyGroupBuilder newGroup = StudyGroup.builder();
            newGroup.id(id).name(group.getName()).login(group.getLogin()).coordinates(group.getCoordinates()).creationDate(group.getCreationDate()).studentsCount(group.getStudentsCount()).expelledStudents(group.getExpelledStudents()).shouldBeExpelled(group.getShouldBeExpelled()).formOfEducation(group.getFormOfEducation()).groupAdmin(group.getGroupAdmin());
            StudyGroup groupToAdd = newGroup.build();
            groups.add(groupToAdd);
            ids.put(groupToAdd.getId(), groupToAdd);
            MyLogger.info("Successfully add element in DB");
            return "Создание StudyGroup завершено! Его id: " + groupToAdd.getId() + "\n";
        }
    }

    public synchronized String show(StudyGroup el) {
        return el.toString();
    }

    public synchronized void clear(String login) {
        List<Integer> idsToRemove = new ArrayList();

        for(StudyGroup g : groups) {
            if (g.getLogin().equals(login)) {
                idsToRemove.add(g.getId());
            }
        }

        for(Integer id : idsToRemove) {
            this.remove_by_id(id, login);
        }

    }

    public synchronized String remove_head(String login) {
        if (!groups.isEmpty()) {
            StudyGroup head = (StudyGroup)groups.get(0);
            Integer id = head.getId();
            this.remove_by_id(id, login);
            return this.show(head);
        } else {
            return "Коллекция пуста!";
        }
    }

    public synchronized String update(Integer groupid, StudyGroup group, String login) {
        int index = -1;

        for(int i = 0; i < groups.size(); ++i) {
            StudyGroup el = groups.get(i);
            if (el.getId() == groupid) {
                index = i;
                break;
            }
        }

        try {
            if (index != -1) {
                StudyGroup.StudyGroupBuilder newGroup = StudyGroup.builder();
                newGroup.id(groupid).name(group.getName()).login(login).coordinates(group.getCoordinates()).creationDate(group.getCreationDate()).studentsCount(group.getStudentsCount()).expelledStudents(group.getExpelledStudents()).shouldBeExpelled(group.getShouldBeExpelled()).formOfEducation(group.getFormOfEducation()).groupAdmin(group.getGroupAdmin());
                StudyGroup groupToUpdate = newGroup.build();
                Integer id = RequestRunner.dbmanager.updateStudyGroup(groupToUpdate, groupid, login);
                if (id == -1) {
                    MyLogger.info("Unsuccessfully update element in DB");
                    return "Ошибка при обновлении элемента в БД \n";
                } else if (id == -11) {
                    MyLogger.info("Client has not roots");
                    return "Вы не можете изменять чужие элементы коллекции\n";
                } else {
                    ids.remove(newGroup);
                    groups.set(index, groupToUpdate);
                    ids.put(group.getId(), groupToUpdate);
                    return "Объект под старым id " + groupid + " был изменён\n";
                }
            } else {
                return "Объект с указанным id не найден\n";
            }
        } catch (Exception e) {
            MyLogger.info(e.getMessage());
            return "";
        }
    }
}
