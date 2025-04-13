//https://pokemondb.net/move/dark-pulse
package mymoves.croagunk;
import ru.ifmo.se.pokemon.*;

public final class DarkPulse extends SpecialMove {
    public DarkPulse() {
        super(Type.DARK, 80, 100);
    }

    private boolean flag = false;

    @Override
    protected void applyOppEffects(Pokemon p) {
        if (Math.random() >= 0.3) {
            flag = true;
            Effect.flinch(p);
        }
    }

    @Override
    protected String describe() {
        if (flag) {
            return "использует Dark Pulse";
        }
        return ("При использовании Dark Pulse наложен страх!");
    }
}
