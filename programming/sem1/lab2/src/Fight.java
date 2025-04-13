import ru.ifmo.se.pokemon.Battle;
import mypokemons.*;

public final class Fight {
    public static void main(String[] args) {
        Battle fight = new Battle();
        fight.addAlly(new Croagunk("Сro", 3));
        fight.addAlly(new Flabebe("Flabby", 3));
        fight.addAlly(new Floette("Flotty", 3));
        fight.addFoe(new Florges("Flor", 3));
        fight.addFoe(new Heatran("Heatty", 3));
        fight.addFoe(new Toxicroak("Toxic", 3));
        fight.go();
    }
}
