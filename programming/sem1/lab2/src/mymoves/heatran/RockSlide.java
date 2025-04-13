//https://pokemondb.net/move/rock-slide
package mymoves.heatran;
import ru.ifmo.se.pokemon.*;

public final class RockSlide extends PhysicalMove{
    public RockSlide () {
        super(Type.ROCK, 75, 90);
    }
    private boolean flag = false;

    @Override
    protected void applyOppEffects(Pokemon p) {
        if (Math.random() >= 0.03) {
            flag = true;
            Effect.flinch(p);
        }
    }

    @Override
    protected String describe() {
        if (flag) {
            return "использует Rock Slide";
        }
        return ("При использовании Rock Slide наложен страх!");
    }
}
