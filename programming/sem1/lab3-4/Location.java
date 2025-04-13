package story;

import java.util.Arrays;
import java.util.Random;
import java.util.ArrayList;

abstract class Location {
    protected ArrayList<Person> people = new ArrayList<Person>(); // Список людей в локации

    abstract void describeLocation();

    void isAnybodyThere() {
        if (people.size() == 0) {
            System.out.println("В " + this.getClass().getSimpleName() + " никого нет!");
        } else {
            System.out.println("Сейчас в " + this.getClass().getSimpleName() + " находятся:");
            for (Person p : people) {
                System.out.print(p.getName() + "\n");
            }
        }
    }
}

class Kitchen extends Location {
    private ArrayList<String> fridge = new ArrayList<String>(Arrays.asList("Pizza", "Soup", "Lasagna", "Pancakes"));


    @Override
    void describeLocation() {
        System.out.println("Кухня: любимое место в доме. Здесь можно подкрепиться (если холодильник не пустой) и восстановить хп, если здоровье достаточно низкое");
    }

    void fridge(Person p) {
        if (fridge.size() == 0) {
            System.out.println("Холодильник пуст((");
        } else {
            Random rand = new Random();
            int index = rand.nextInt(fridge.size());
            String food = fridge.get(index);
            fridge.remove(index);
            System.out.println(p.getName() + " съел(а) из холодильника " + food + ", состояние улучшилось");
            try {
                p.setHealth(p.getHealth() + 5);
            } catch (InvalidHealthException e) {
                System.out.println("Здоровье персонажа не было изменено, поскольку и так довольно высокое");
            }
        }

    }

    void flowers(Person p) {
        System.out.println(p.getName() + " любуется цветами, раставленными на кухне");
        p.setEmotion("HAPPY");
    }

}


class Bedroom extends Location {
    @Override
    void describeLocation() {
        System.out.println("Спальня: здесь можно поспать, чтобы улучшилось настроение и восстановились силы");
    }

    void sleeping(Person p) {
        Random rand = new Random();
        int chance = rand.nextInt(2);
        System.out.println(p.getName() + " пошёл (пошла) спать");
        switch (chance) {
            case 0:
                System.out.println("Сон пошёл " + p.getName() + " на пользу");
                p.setEmotion("NEUTRAL");
                try {
                    p.setHealth(p.getHealth() + 5);
                } catch (InvalidHealthException e) {
                    System.out.println("Здоровье персонажа не было изменено, поскольку и так довольно высокое");
                }
                break;
            case 1:
                System.out.println("Работяга " + p.getName() + " не выспался (выспалась)");
                p.setEmotion("ANGRY");
                try {
                    p.setHealth(p.getHealth() - 5);
                } catch (InvalidHealthException e) {
                }
                break;
        }
    }

    void watchTV(Person p) {
        System.out.print(p.getName() + " сейчас смотрит телевизор. Он(а) включил(а) ");
        Random rand = new Random();
        int chanel = rand.nextInt(8);
        switch (chanel) {
            case 0:
                System.out.println("\"Завтрак с шефом\": программа с рецептами для идеального завтрака от лучших шеф-поваров.");
                p.setEmotion("HAPPY");
                break;
            case 1:
                System.out.println("\"История в кадре\": документальные фильмы о значимых событиях мировой истории.");
                p.setEmotion("NEUTRAL");
                break;
            case 2:
                System.out.println("\"Путеводитель по звездам\": астрологическое шоу, где обсуждают влияние планет и звезд на жизнь человека.");
                p.setEmotion("NEUTRAL");
                break;
            case 3:
                System.out.println("\"Городские легенды\": мистические истории и загадочные происшествия из разных уголков мира.");
                p.setEmotion("SAD");
                break;
            case 4:
                System.out.println("\"Тренды и технологии\": программа о новых технологических гаджетах, стартапах и трендах в мире технологий.");
                p.setEmotion("HAPPY");
                break;
            case 5:
                System.out.println("\"Секреты мастерства\": советы по рукоделию, кулинарии и творчеству.");
                p.setEmotion("HAPPY");
                break;
            case 6:
                System.out.println("\"Экспресс-новости\": краткие, но важные новости из мира политики, экономики и культуры.");
                p.setEmotion("ANGRY");
                break;
            case 7:
                System.out.println("\"Большая сцена\": шоу с живыми выступлениями театральных и вокальных коллективов.");
                p.setEmotion("HAPPY");
                break;
        }
    }
}


class LivingRoom extends Location {
    @Override
    void describeLocation() {
        System.out.println("Гостиная: здесь можно поиграть в крестики-нолики и посмотреть телевизор. Возможная побочка: смена настроения");
    }

    void ticTacToe(Person p1, Person p2) {
        if (this.people.contains(p1) && this.people.contains(p2)) {
            System.out.println(p1.getName() + " и " + p2.getName() + " играют в крестики-нолики");
            String[][] board = new String[3][3];

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    board[i][j] = " "; // Пустые клетки
                }
            }

            Random rand = new Random();
            String currentPlayer = "X"; // Игрок с крестиками начинает

            // Цикл игры
            for (int turn = 0; turn < 9; turn++) {
                int row, col;
                do {
                    row = rand.nextInt(3);
                    col = rand.nextInt(3);
                } while (!board[row][col].equals(" ")); // Повторяем, пока не найдём пустую клетку

                // Делает ход
                board[row][col] = currentPlayer;
                printBoard(board);

                if (checkWinner(board, currentPlayer)) {
                    if (currentPlayer.equals("X")) {
                        System.out.println("Игрок " + p1.getName() + " победил!");
                        p1.setEmotion("HAPPY");
                        p2.setEmotion("SAD");
                    } else {
                        System.out.println("Игрок " + p2.getName() + " победил!");
                        p2.setEmotion("HAPPY");
                        p1.setEmotion("SAD");
                    }
                    return;
                }

                // Меняем игрока
                currentPlayer = (currentPlayer.equals("X")) ? "O" : "X";
            }

            System.out.println("Ничья! Поле заполнено");
        } else {
            System.out.println("В гостиной нет всех указанных персонажей для игры!");
        }
    }

    private void printBoard(String[][] board) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(" " + board[i][j] + " ");
                if (j < 2) System.out.print("|");
            }
            System.out.println();
            if (i < 2) System.out.println("---|---|---");
        }
        System.out.println();
    }

    // Метод для проверки победителя
    private boolean checkWinner(String[][] board, String player) {

        // Проверка строк
        for (int i = 0; i < 3; i++) {
            if (board[i][0].equals(player) && board[i][1].equals(player) && board[i][2].equals(player)) {
                return true;
            }
        }

        // Проверка столбцов
        for (int j = 0; j < 3; j++) {
            if (board[0][j].equals(player) && board[1][j].equals(player) && board[2][j].equals(player)) {
                return true;
            }
        }

        if (board[0][0].equals(player) && board[1][1].equals(player) && board[2][2].equals(player)) {
            return true;
        }
        if (board[0][2].equals(player) && board[1][1].equals(player) && board[2][0].equals(player)) {
            return true;
        }

        return false;
    }
}
