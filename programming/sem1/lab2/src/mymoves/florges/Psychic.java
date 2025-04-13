//https://pokemondb.net/move/psychic
package mymoves.florges;
import ru.ifmo.se.pokemon.*;

public final class Psychic extends SpecialMove {
    public Psychic() {
        super(Type.PSYCHIC, 90, 100);
    }
    @Override
    protected String describe() {
        return "Проведена атака Psychic со снижением защиты!";
    }

    @Override
    protected void applyOppEffects(Pokemon p) {
        Effect e = new Effect().chance(0.1).stat(Stat.SPECIAL_DEFENSE, -1);
        p.addEffect(e);
    }
}
