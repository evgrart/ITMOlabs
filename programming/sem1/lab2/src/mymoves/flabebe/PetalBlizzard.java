//https://pokemondb.net/move/petal-blizzard
package mymoves.flabebe;
import ru.ifmo.se.pokemon.*;

public class PetalBlizzard extends PhysicalMove {
    public PetalBlizzard() {
        super(Type.GRASS, 90, 100);
    }

    @Override
    protected String describe() {
        return "Проведена атака Petal Blizzard!";
    }
}
