package main_classes;

import java.time.LocalDateTime;

/**
 * Класс описывает объект, который может быть полем {@link StudyGroup}, а именно - groupAdmin
 */
public class Person {
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

    /**
     * Сравнивает Person по всем полям. Если всех из них в точности равны, то это один и тот же чел
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        if (height != person.height) return false;
        if (weight != person.weight) return false;
        if (name != this.name) return false;
        if (!passportID.equals(person.passportID)) return false;
        return birthday.equals(person.birthday);
    }

    @Override
    public String toString() {
        return String.format("name: %s\nbirthday: %s\nheight: %d\nweight: %d\npassportID: %s",
                name, birthday, height, weight, passportID);
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getBirthday() {
        return birthday;
    }

    public long getHeight() {
        return height;
    }

    public int getWeight() {
        return weight;
    }

    public String getPassportID() {
        return passportID;
    }

    /**
     * @return Возвращает билдер для дальнейшей инициализации Person
     */
    public static PersonBuilder builder() {
        return new PersonBuilder();
    }

    /**
     * Билдер, чтоб не создавать миллион конструкторов
     */
    public static class PersonBuilder {
        private String name;
        private LocalDateTime birthday;
        private long height;
        private int weight;
        private String passportID;

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

        /**
         * Собираем Person из инициализированных полей (незаданные поля принимают значения по умолчанию)
         */
        public Person build() {
            return new Person(name, birthday, height, weight, passportID);
        }
    }

}