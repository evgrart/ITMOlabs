package story;

public class Main {
    public static void main(String[] args) {
        try {
            Bedroom bedroom = new Bedroom();
            Kitchen kitchen = new Kitchen();
            LivingRoom living = new LivingRoom();
            StoryTeller story = new StoryTeller();
            int distance = 60;

            Child malish = new Child("Malish", 150, Emotion.SAD, kitchen);
            Child betan = new Child("Betan", 160, Emotion.NEUTRAL, living);
            Child bosse = new Child("Bosse", 160, Emotion.ANGRY, living);
            Adult mother = new Adult("Mother", 190, Emotion.HAPPY, bedroom);
            Child[] children = {malish, betan, bosse};

            story.routine(children, mother, living, bedroom, kitchen);
            story.getDamage(children, mother, distance);

        } catch (InvalidHealthException e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}
