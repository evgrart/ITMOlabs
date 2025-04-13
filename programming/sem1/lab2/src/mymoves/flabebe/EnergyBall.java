//https://pokemondb.net/move/energy-ball
package mymoves.flabebe;
import ru.ifmo.se.pokemon.*;

public final class EnergyBall extends SpecialMove {
    public EnergyBall() {
        super(Type.GRASS, 90, 100);
    }

    @Override
    protected void applyOppEffects(Pokemon p) {
        Effect e = new Effect().chance(0.1).stat(Stat.SPECIAL_DEFENSE, -1);
        p.addEffect(e);
    }

    @Override
    protected String describe() {
        return "Проведена атака Energy Ball, спец защита снижена!";
    }
}
