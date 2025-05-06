//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package general.main_classes;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class StudyGroup implements Comparable<StudyGroup>, Serializable {
    private final int id;
    private final String login;
    private final String name;
    private final Coordinates coordinates;
    private final ZonedDateTime creationDate;
    private final long studentsCount;
    private final int expelledStudents;
    private final long shouldBeExpelled;
    private final FormOfEducation formOfEducation;
    private final Person groupAdmin;

    private StudyGroup(int id, String login, String name, Coordinates coordinates, ZonedDateTime creationDate, long studentsCount, int expelledStudents, long shouldBeExpelled, FormOfEducation formOfEducation, Person groupAdmin) {
        this.id = id;
        if (creationDate == null) {
            this.creationDate = ZonedDateTime.now();
        } else {
            this.creationDate = creationDate;
        }

        this.name = name;
        this.login = login;
        this.coordinates = coordinates;
        this.studentsCount = studentsCount;
        this.expelledStudents = expelledStudents;
        this.shouldBeExpelled = shouldBeExpelled;
        this.formOfEducation = formOfEducation;
        this.groupAdmin = groupAdmin;
    }

    public static StudyGroupBuilder builder() {
        return new StudyGroupBuilder();
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Coordinates getCoordinates() {
        return this.coordinates;
    }

    public String getLogin() {
        return this.login;
    }

    public ZonedDateTime getCreationDate() {
        return this.creationDate;
    }

    public long getStudentsCount() {
        return this.studentsCount;
    }

    public int getExpelledStudents() {
        return this.expelledStudents;
    }

    public long getShouldBeExpelled() {
        return this.shouldBeExpelled;
    }

    public String toString() {
        return String.format("id: %d\nowner: %s\nname: %s\ncoordinates: %s\ncreationDate: %s\nstudentsCount: %d\nexpelledStudents: %d\nshouldBeExpelled: %d\nformOfEducation: %s\ngroupAdmin:\n%s", this.id, this.login, this.name, this.coordinates, this.creationDate, this.studentsCount, this.expelledStudents, this.shouldBeExpelled, this.formOfEducation, this.groupAdmin != null ? this.groupAdmin : "null");
    }

    public String toJson() {
        StringBuilder json = new StringBuilder("\n  {\n");
        json.append("  \"id\": ").append(this.id).append(",\n");
        json.append("  \"name\": \"").append(this.name).append("\",\n");
        json.append("  \"coordinates\": {\n");
        json.append("    \"x\": ").append(this.coordinates.getX()).append(",\n");
        json.append("    \"y\": ").append(this.coordinates.getY()).append("\n");
        json.append("  },\n");
        json.append("  \"creationDate\": \"").append(this.creationDate.toString()).append("\",\n");
        json.append("  \"studentsCount\": ").append(this.studentsCount).append(",\n");
        json.append("  \"expelledStudents\": ").append(this.expelledStudents).append(",\n");
        json.append("  \"shouldBeExpelled\": ").append(this.shouldBeExpelled).append(",\n");
        json.append("  \"formOfEducation\": ").append(this.formOfEducation == null ? "null" : "\"" + String.valueOf(this.formOfEducation) + "\"").append(",\n");
        json.append("  \"groupAdmin\": ");
        if (this.groupAdmin != null) {
            json.append("{\n");
            json.append("    \"name\": \"").append(this.groupAdmin.getName()).append("\",\n");
            json.append("    \"birthday\": \"").append(this.groupAdmin.getBirthday().toString()).append("\",\n");
            json.append("    \"height\": ").append(this.groupAdmin.getHeight()).append(",\n");
            json.append("    \"weight\": ").append(this.groupAdmin.getWeight()).append(",\n");
            json.append("    \"passportID\": \"").append(this.groupAdmin.getPassportID()).append("\"\n");
            json.append("  }");
        } else {
            json.append("null");
        }

        json.append("\n  }");
        return json.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj != null && this.getClass() == obj.getClass()) {
            StudyGroup that = (StudyGroup)obj;
            return this.id == that.id;
        } else {
            return false;
        }
    }

    public Person getGroupAdmin() {
        return this.groupAdmin;
    }

    public int compareTo(StudyGroup that) {
        return this.id - that.id;
    }

    public FormOfEducation getFormOfEducation() {
        return this.formOfEducation;
    }

    public static class StudyGroupBuilder implements Serializable {
        private int id;
        private String name;
        private String login;
        private ZonedDateTime creationDate;
        private Coordinates coordinates;
        private long studentsCount;
        private int expelledStudents;
        private long shouldBeExpelled;
        private FormOfEducation formOfEducation;
        private Person groupAdmin;

        public StudyGroupBuilder() {
        }

        public StudyGroupBuilder name(String name) {
            this.name = name;
            return this;
        }

        public StudyGroupBuilder id(int id) {
            this.id = id;
            return this;
        }

        public StudyGroupBuilder login(String login) {
            this.login = login;
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

        public StudyGroup build() {
            return new StudyGroup(this.id, this.login, this.name, this.coordinates, this.creationDate, this.studentsCount, this.expelledStudents, this.shouldBeExpelled, this.formOfEducation, this.groupAdmin);
        }
    }
}
