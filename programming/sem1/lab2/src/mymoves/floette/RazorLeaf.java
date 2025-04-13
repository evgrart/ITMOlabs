//https://pokemondb.net/move/razor-leaf
package mymoves.floette;
import ru.ifmo.se.pokemon.*;

public final class RazorLeaf extends PhysicalMove {
    public RazorLeaf() {
        super(Type.GRASS, 55, 95);
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
    @Override
    protected String describe() {
        return "проведена атака Razor Leaf с повышенным шансом крита!";
    }
}
