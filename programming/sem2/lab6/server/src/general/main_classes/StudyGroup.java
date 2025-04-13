package general.main_classes;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Класс, все объекты которого находятся в коллекции {@link server.utility.CollectionManager#groups}
 */
public class StudyGroup implements Comparable<StudyGroup>, Serializable {
    private final int id;
    private final String name;
    private final Coordinates coordinates;
    private final ZonedDateTime creationDate;
    private final long studentsCount;
    private final int expelledStudents;
    private final long shouldBeExpelled;
    private final FormOfEducation formOfEducation;
    private final Person groupAdmin;

    /**
     * В конструкторе проверяем, если мы передаем значения id/creationDate по умолчанию (то есть мы создаем новый объект), то генерируем/устанавливаем поля; иначе - инициализируем значениями того же объекта из прошлой сессии
     */
    private StudyGroup(int id, String name, Coordinates coordinates, ZonedDateTime creationDate, long studentsCount, int expelledStudents, long shouldBeExpelled, FormOfEducation formOfEducation, Person groupAdmin) {
        if (id == 0) {
            this.id = this.hashCode();
        } else {
            this.id = id;
        }
        if (creationDate == null) {
            this.creationDate = ZonedDateTime.now();
        } else {
            this.creationDate = creationDate;
        }

        this.name = name;
        this.coordinates = coordinates;
        this.studentsCount = studentsCount;
        this.expelledStudents = expelledStudents;
        this.shouldBeExpelled = shouldBeExpelled;
        this.formOfEducation = formOfEducation;
        this.groupAdmin = groupAdmin;
    }

    /**
     * Возвращает билдер для дальнейшей инициализации Person
     */
    public static StudyGroupBuilder builder() {
        return new StudyGroupBuilder();
    }

    public int getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return String.format("id: %d\nname: %s\ncoordinates: %s\ncreationDate: %s\nstudentsCount: %d\nexpelledStudents: %d\nshouldBeExpelled: %d\nformOfEducation: %s\ngroupAdmin:\n%s",
                id, name, coordinates, creationDate, studentsCount, expelledStudents, shouldBeExpelled, formOfEducation, groupAdmin);
    }


    /**
     * Для парсинга в json ибо либами нельзя(
     */
    public String toJson() {
        StringBuilder json = new StringBuilder("\n  {\n");
        json.append("  \"id\": ").append(id).append(",\n");
        json.append("  \"name\": \"").append(name).append("\",\n");
        json.append("  \"coordinates\": {\n");
        json.append("    \"x\": ").append(coordinates.getX()).append(",\n");
        json.append("    \"y\": ").append(coordinates.getY()).append("\n");
        json.append("  },\n");
        json.append("  \"creationDate\": \"").append(creationDate.toString()).append("\",\n");
        json.append("  \"studentsCount\": ").append(studentsCount).append(",\n");
        json.append("  \"expelledStudents\": ").append(expelledStudents).append(",\n");
        json.append("  \"shouldBeExpelled\": ").append(shouldBeExpelled).append(",\n");

        json.append("  \"formOfEducation\": ").append(formOfEducation == null ? "null" : "\"" + formOfEducation + "\"").append(",\n");

        json.append("  \"groupAdmin\": ");
        if (groupAdmin != null) {
            json.append("{\n");
            json.append("    \"name\": \"").append(groupAdmin.getName()).append("\",\n");
            json.append("    \"birthday\": \"").append(groupAdmin.getBirthday().toString()).append("\",\n");
            json.append("    \"height\": ").append(groupAdmin.getHeight()).append(",\n");
            json.append("    \"weight\": ").append(groupAdmin.getWeight()).append(",\n");
            json.append("    \"passportID\": \"").append(groupAdmin.getPassportID()).append("\"\n");
            json.append("  }");
        } else {
            json.append("null");
        }
        json.append("\n  }");

        return json.toString();
    }

    /**
     * Сравниваем объекты по их айдишнику. id детерминирован
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj != null && this.getClass() == obj.getClass()) {
            StudyGroup that = (StudyGroup) obj;
            return this.id == that.id;
        } else {
            return false;
        }
    }

    public Person getGroupAdmin() {
        return groupAdmin;
    }

    public int compareTo(StudyGroup that) {
        return this.id - that.id;
    }

    public FormOfEducation getFormOfEducation() {
        return formOfEducation;
    }

    /**
     * Билдер, чтоб не создавать миллион конструкторов
     */
    public static class StudyGroupBuilder implements Serializable {
        private int id;
        private String name;
        private ZonedDateTime creationDate;
        private Coordinates coordinates;
        private long studentsCount;
        private int expelledStudents;
        private long shouldBeExpelled;
        private FormOfEducation formOfEducation;
        private Person groupAdmin;

        public StudyGroupBuilder name(String name) {
            this.name = name;
            return this;
        }

        public StudyGroupBuilder id(int id) {
            this.id = id;
            return this;
        }

        public StudyGroupBuilder coordinates(Coordinates coordinates) {
            this.coordinates = coordinates;
            return this;
        }

        public StudyGroupBuilder creationDate(ZonedDateTime creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public StudyGroupBuilder studentsCount(long studentsCount) {
            this.studentsCount = studentsCount;
            return this;
        }

        public StudyGroupBuilder expelledStudents(int expelledStudents) {
            this.expelledStudents = expelledStudents;
            return this;
        }

        public StudyGroupBuilder shouldBeExpelled(long shouldBeExpelled) {
            this.shouldBeExpelled = shouldBeExpelled;
            return this;
        }

        public StudyGroupBuilder formOfEducation(FormOfEducation formOfEducation) {
            this.formOfEducation = formOfEducation;
            return this;
        }

        public StudyGroupBuilder groupAdmin(Person groupAdmin) {
            this.groupAdmin = groupAdmin;
            return this;
        }

        /**
         * Собираем StudyGroup из инициализированных полей (незаданные поля принимают значения по умолчанию)
         */
        public StudyGroup build() {
            return new StudyGroup(id, name, coordinates, creationDate, studentsCount, expelledStudents, shouldBeExpelled, formOfEducation, groupAdmin);
        }
    }
}