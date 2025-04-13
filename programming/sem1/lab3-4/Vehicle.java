package story;

import java.util.Random;

interface Transport {
    int arriveTimeInApartment();

    int arriveTimeInHospital();

    void depart();
}

class TransportException extends Exception {
    public TransportException(String message) {
        super(message);
    }
}

class Ambulance implements Transport {
    private boolean status = false;
    private final Random random = new Random();
    private final int distance;
    private final int queue = random.nextInt(10) + 1;
    private final int averageSpeed = (random.nextInt(8) + 5) * 10;

    public Ambulance(int distance) {
        this.distance = distance;
    }

    public void getQueueLevel() {
        System.out.println("Сейчас пробки в городе достигают " + queue + " баллов");
    }

    @Override
    public void depart() {
        try {
            if (this.status) {
                System.out.println("Травмы незначительные - скорая помощь выехала! Приедет через " + this.arriveTimeInApartment() + " минут");
            } else {
                throw new TransportException("Транспорт не был вызван");
            }
        } catch (TransportException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public int arriveTimeInApartment() {
        return (int) ((distance * 60 / averageSpeed) * (1 + queue / 15.0));
    }

    @Override
    public int arriveTimeInHospital() {
        return (int) ((distance * 60 / averageSpeed) * (1 + queue / 15.0) * 0.85);
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}

class Helicopter implements Transport {
    private boolean status = false;
    private final int distance;
    private final int averageSpeed = 220;
    private final double randomValue = Math.random();

    public Helicopter(int distance) {
        this.distance = distance;
    }

    @Override
    public void depart() {
        try {
            if (this.status) {
                System.out.println("Травмы серьёзные - вертолёт вылетел! Прилетит через " + this.arriveTimeInApartment() + " минут");
            } else {
                throw new TransportException("Транспорт не был вызван");
            }
        } catch (TransportException e) {
            System.out.println(e.getMessage());
        }
    }

    public void fallingProbability(Child c) throws InvalidHealthException {
        if (randomValue >= 0.97) {
            c.setHealth(0);
            System.out.println("О ужас! Из-за сильной турбулентности и халатности врачей ребёнок выпал из вертолёта! Похоже, это конец");
            new StoryTeller().funeral(c);
        }
    }

    @Override
    public int arriveTimeInApartment() {
        return (int) ((distance * 60 / averageSpeed) + 15);
    }

    @Override
    public int arriveTimeInHospital() {
        return (int) ((distance * 60 / averageSpeed) + 20);
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
