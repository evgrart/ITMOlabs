//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package general.main_classes;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Person implements Serializable, Comparable<Person> {
    private String name;
    private LocalDateTime birthday;
    private long height;
    private int weight;
    private String passportID;

    public Person(String name, LocalDateTime birthday, long height, int weight, String passportID) {
        this.name = name;
        this.birthday = birthday;
        this.height = height;
        this.weight = weight;
        this.passportID = passportID;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Person person = (Person)o;
            if (this.height != person.height) {
                return false;
            } else if (this.weight != person.weight) {
                return false;
            } else if (this.name != this.name) {
                return false;
            } else {
                return !this.passportID.equals(person.passportID) ? false : this.birthday.equals(person.birthday);
            }
        } else {
            return false;
        }
    }

    public int compareTo(Person o) {
        int thisSum = this.getPassportID().chars().sum();
        int otherSum = o.getPassportID().chars().sum();
        return Integer.compare(thisSum, otherSum);
    }

    public String toString() {
        return String.format("name: %s\nbirthday: %s\nheight: %d\nweight: %d\npassportID: %s", this.name, this.birthday, this.height, this.weight, this.passportID);
    }

    public String getName() {
        return this.name;
    }

    public LocalDateTime getBirthday() {
        return this.birthday;
    }

    public long getHeight() {
        return this.height;
    }

    public int getWeight() {
        return this.weight;
    }

    public String getPassportID() {
        return this.passportID;
    }

    public static PersonBuilder builder() {
        return new PersonBuilder();
    }

    public static class PersonBuilder {
        private String name;
        private LocalDateTime birthday;
        private long height;
        private int weight;
        private String passportID;

        public PersonBuilder() {
        }

        public PersonBuilder name(String name) {
            this.name = name;
            return this;
        }

        public PersonBuilder birthday(LocalDateTime birthday) {
            this.birthday = birthday;
            return this;
        }

        public PersonBuilder height(long height) {
            this.height = height;
            return this;
        }

        public PersonBuilder weight(int weight) {
            this.weight = weight;
            return this;
        }

        public PersonBuilder passportID(String passportID) {
            this.passportID = passportID;
            return this;
        }

        public Person build() {
            return new Person(this.name, this.birthday, this.height, this.weight, this.passportID);
        }
    }
}
