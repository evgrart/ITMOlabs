package story;

import java.util.Random;

enum Emotion {
    SAD, ANGRY, HAPPY, NEUTRAL;
}

record Status(String name, int health, Emotion emotion, Location location) {
}

class InvalidHealthException extends Exception {
    public InvalidHealthException(String message) {
        super(message);
    }
}

abstract class Person {
    protected Status person;

    public Person(String name, int health, Emotion emotion, Location location) throws InvalidHealthException {
        this.person = new Status(name, health, emotion, location);
        location.people.add(this);
        if (health > 200) {
            throw new InvalidHealthException("Здоровье взрослого не может быть больше 200");
        }
    }

    public void setEmotion(String emotionInput) {
        try {
            Emotion emotion = Emotion.valueOf(emotionInput.toUpperCase());
            this.person = new Status(person.name(), person.health(), emotion, person.location());
            System.out.println("Теперь " + this.person.name() + " имеет состояние " + emotion);
        } catch (IllegalArgumentException e) {
            System.err.println("Такой эмоции нет!");
        }
    }

    public Emotion getEmotion() {
        return this.person.emotion();
    }

    public void setHealth(int health) throws InvalidHealthException {
        if (health >= 0 && health <= 200) {
            int oldHealth = this.person.health();
            this.person = new Status(person.name(), health, person.emotion(), person.location());
            System.out.print("Здоровье персонажа " + this.person.name() + " стало равным " + health);
            if ((health - oldHealth) > 0) {
                System.out.println(" (повысилось на " + (health - oldHealth) + ")");
            } else {
                System.out.println(" (уменьшилось на " + (oldHealth - health) + ")");
            }
        } else {
            throw new InvalidHealthException("Здоровье взрослого должно быть <= 200");
        }
    }

    public int getHealth() {
        return this.person.health();
    }

    public String getName() {
        return this.person.name();
    }

    public Location getLocation() {
        return this.person.location();
    }

    public void changeLocation(Location location) {
        System.out.println("Местоположение персонажа " + this.getName() + " с " + this.getLocation().getClass().getSimpleName() + " сменилось на " + location.getClass().getSimpleName());
        int index = this.getLocation().people.indexOf(this);
        this.getLocation().people.remove(index);
        location.people.add(this);
    }

    abstract void accidentReaction(Person p);

    abstract void needHelp(Person p);

    @Override
    public boolean equals(Object obj) {
        Person p = (Person) obj;
        return person.name().equals(p.person.name()) &&
                person.health() == p.person.health() &&
                person.emotion().equals(p.person.emotion());
    }

    @Override
    public String toString() {
        String person = "Имя: " + this.getName() + "\nЗдоровье: " + this.getHealth() + "\nСостояние: " + this.getEmotion() + "\nМестоположение: " + this.getLocation().getClass().getSimpleName();
        return person;
    }

    @Override
    public int hashCode() {
        return this.person.health() ^ 123;
    }

}

class Child extends Person {
    public Child(String name, int health, Emotion emotion, Location location) throws InvalidHealthException {
        super(name, health, emotion, location);
        if (health > 160) {
            throw new InvalidHealthException("Здоровье ребенка не может быть больше 160");
        }
    }

    @Override
    public void accidentReaction(Person p) {
        System.out.println("Реакция " + this.getName() + " на здоровье " + p.getName() + ":");
        if (p.getHealth() >= 110) {
            System.out.println(p.getName() + "Здоров(а), чего волноваться?!");
        } else {
            switch (this.getEmotion()) {
                case SAD:
                    System.out.println("Это так печально... Надеюсь, он(а) скоро поправится.");
                    break;
                case ANGRY:
                    System.out.println("Теперь мне нельзя играть! Это так несправедливо!");
                    this.setEmotion("SAD");
                    this.getEmotion();
                    break;
                case HAPPY:
                    System.out.println("Давай поиграем, когда ты станешь лучше! Я уже придумал игру.");
                    this.setEmotion("NEUTRAL");
                    this.getEmotion();
                    break;
                case NEUTRAL:
                    System.out.println("...ну ладно, надеюсь, ты поправишься.");
                    this.setEmotion("SAD");
                    this.getEmotion();
                    break;
            }
        }
    }

    public void randomMoodChange() {
        Random rand = new Random();
        Emotion[] emotions = Emotion.values();
        Emotion newEmotion = emotions[rand.nextInt(emotions.length)];
        System.out.println("Неожиданно настроение " + this.getName() + " резко изменилось");
        this.setEmotion(newEmotion.toString());
    }

    @Override
    public void setHealth(int health) throws InvalidHealthException {
        if (health >= 0 && health <= 160) {
            super.setHealth(health);
        } else {
            throw new InvalidHealthException("Здоровье ребенка должно быть <= 160");
        }
    }

    public void askForHelp(Adult adult) {
        System.out.println(this.getName() + " жалуется " + adult.getName() + " на состояние здоровья");
    }

    @Override
    public void needHelp(Person p) {
        System.out.println("Ребёнок не может самостоятельно вызвать скорую! Пусть это сделает взрослый");
    }
}

class Adult extends Person {
    public Adult(String name, int health, Emotion emotion, Location location) throws InvalidHealthException {
        super(name, health, emotion, location);
        if (health > 200) {
            throw new InvalidHealthException("Здоровье взрослого не может быть больше 200");
        }
    }

    @Override
    public void accidentReaction(Person p) {
        System.out.println("Реакция " + this.getName() + " на здоровье " + p.getName() + ":");
        if (p.getHealth() < 110) {
            System.out.println(p.getName() + " что с тобой? Это нехорошо. Схожу за первой помощью");
            try {
                p.setHealth(p.getHealth() + 15);
            } catch (InvalidHealthException e) {
            }
        } else {
            System.out.println(p.getName() + "Здоров(а), чего пристали?");
        }
    }

    @Override
    public void needHelp(Person p) {
        System.out.println(this.getName() + " уже вызвал(а) скорую помощь для " + p.getName());
    }

    public void currentDoing(Adult p) {
        Random rand = new Random();
        int task = rand.nextInt(4);
        switch (task) {
            case 0:
                System.out.println(p.getName() + " сейчас моет полы в квартире");
                break;
            case 1:
                System.out.println(p.getName() + " сейчас готовит обед");
                break;
            case 2:
                System.out.println(p.getName() + " сейчас играет в дурака онлайн");
                break;
            case 3:
                System.out.println(p.getName() + " сейчас поливает цветы в квартире");
                break;
        }
    }
}
