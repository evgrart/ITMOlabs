//https://pokemondb.net/move/sludge-bomb
package mymoves.croagunk;
import ru.ifmo.se.pokemon.*;

public final class SludgeBomb extends SpecialMove {
    public SludgeBomb() {
        super(Type.POISON, 90, 100);
    }

    private boolean flag = false;

    @Override
    protected void applyOppEffects(Pokemon p) {
        if (Math.random() >= 0.3) {
            flag = true;
            Effect.poison(p);
        }
    }

    @Override
    protected String describe() {
        if (flag) {
            return "использует Sludge Bomb";
        }
        return ("При использовании Sludge Bomb наложено отравление!");
    }
}
