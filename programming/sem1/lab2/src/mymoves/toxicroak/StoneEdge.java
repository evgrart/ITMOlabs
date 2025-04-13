//https://pokemondb.net/move/stone-edge
package mymoves.toxicroak;
import ru.ifmo.se.pokemon.*;

public final class StoneEdge extends PhysicalMove {
    public StoneEdge() {
        super(Type.ROCK, 100, 80);
    }
    @Override
    protected String describe() {
        return "проведена атака Stone Edge с повышенным кш!";
    }

    @Override
    public double calcCriticalHit(Pokemon att, Pokemon def) {
        if (att.getStat(Stat.SPEED) / (512.0 / 3) > Math.random()) {
            System.out.println("Критический удар!!");
            return 2.0;
        } else {
            return 1.0;
        }
    }
}
