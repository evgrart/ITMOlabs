//https://pokemondb.net/move/rock-tomb
package mymoves.heatran;
import ru.ifmo.se.pokemon.*;

public final class RockTomb extends PhysicalMove{
    public RockTomb() {
        super(Type.ROCK, 60, 95);
    }
    @Override
    protected String describe() {
        return "использует Rock Tomb, уменьшает скорость!";
    }
    @Override
    protected void applyOppEffects(Pokemon p) {
        Effect e = new Effect().stat(Stat.SPEED, -1);
        p.addEffect(e);
    }
}
