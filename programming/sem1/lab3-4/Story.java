package story;

import story.*;

import java.util.Random;

class StoryTeller {
    public void funeral(Person p) {
        System.out.println();
        System.out.println("Траурное известие: ребёнок " + p.getName() + " покинул(а) этот мир.");
        System.out.println("Организуются похороны. Все собрались, чтобы проводить " + p.getName() + " в последний путь.");
        System.out.println("Родные и близкие вспоминают лучшие моменты из жизни " + p.getName() + ".");
        System.out.println("На месте прощания расставлены цветы и фотографии, звучит печальная музыка.");
        System.out.println("Мы навсегда запомним " + p.getName() + " с любовью и теплотой.");
        System.exit(0);
    }

    public void routine(Child[] children, Adult adult, LivingRoom livingRoom, Bedroom bedroom, Kitchen kitchen) {
        Random random = new Random();
        livingRoom.describeLocation();
        livingRoom.isAnybodyThere();
        livingRoom.ticTacToe(children[1], children[2]);
        bedroom.describeLocation();
        bedroom.isAnybodyThere();
        bedroom.sleeping(adult);
        bedroom.watchTV(adult);
        kitchen.describeLocation();
        kitchen.isAnybodyThere();
        kitchen.flowers(children[0]);
        kitchen.fridge(children[0]);
        children[random.nextInt(2)].randomMoodChange();
        children[1].changeLocation(kitchen);
        kitchen.fridge(children[1]);
    }

    public void getDamage(Child[] children, Adult adult, int distance) {
        System.out.println();
        Random random = new Random();
        Child illChild = children[random.nextInt(3)];
        int damage = random.nextInt(31) + 85;
        try {
            System.out.println("Произошёл несчастный случай!");
            illChild.setHealth(illChild.getHealth() - damage);
            this.illPlote(illChild, children, adult, distance);
        } catch (InvalidHealthException e) {
        }
    }

    public void illPlote(Child illChild, Child[] children, Adult adult, int distance) {
        System.out.println();
        for (Child child : children) {
            if (!child.equals(illChild)) {
                child.accidentReaction(illChild);
            }
        }
        illChild.askForHelp(adult);
        adult.accidentReaction(illChild);
        adult.needHelp(illChild);
        if (illChild.getHealth() >= 60) {
            Ambulance ambulance = new Ambulance(distance);
            ambulance.setStatus(true);
            ambulance.depart();
            ambulance.arriveTimeInApartment();
            if (illChild.getHealth() - ambulance.arriveTimeInApartment() > 0) {
                System.out.println("Скорая прибыла!");
                try {
                    illChild.setHealth(illChild.getHealth() + 20);
                    System.out.println("Скорая выехала в госпиталь");
                    if (illChild.getHealth() - ambulance.arriveTimeInHospital() > 0) {
                        System.out.println(illChild.getName() + " прибыл(а) в госпиталь. Надеемся, он(а) скоро поправится");
                    } else {
                        System.out.println("К сожалению, " + illChild.getName() + " не смог(ла) доехать до госпиталя");
                        this.funeral(illChild);
                    }
                } catch (InvalidHealthException e) {
                }
            } else {
                System.out.println("К сожалению, " + illChild.getName() + " не смог(ла) дождаться скорую...");
                this.funeral(illChild);
            }
        } else {
            Helicopter helicopter = new Helicopter(distance);
            helicopter.setStatus(true);
            helicopter.depart();
            helicopter.arriveTimeInApartment();
            if (illChild.getHealth() - helicopter.arriveTimeInApartment() > 0) {
                System.out.println("Вертолёт прибыл!");
                try {
                    helicopter.fallingProbability(illChild);
                    illChild.setHealth(illChild.getHealth() + 20);
                    System.out.println("Вертолёт вылетел в госпиталь");
                    if (illChild.getHealth() - helicopter.arriveTimeInHospital() > 0) {
                        System.out.println(illChild.getName() + " прилетел(а) в госпиталь. Надеемся, он(а) скоро поправится");
                    } else {
                        System.out.println("К сожалению, " + illChild.getName() + " не смог(ла) добраться до госпиталя");
                        this.funeral(illChild);
                    }
                } catch (InvalidHealthException e) {
                }
            } else {
                System.out.println("К сожалению, " + illChild.getName() + " не смог(ла) дождаться помощь...");
                this.funeral(illChild);
            }

        }

    }

}
