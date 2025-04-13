//https://pokemondb.net/move/flash-cannon
package mymoves.heatran;
import ru.ifmo.se.pokemon.*;

public final class FlashCannon extends SpecialMove{
    public FlashCannon() {
        super(Type.STEEL, 80, 100);
    }
    @Override
    protected String describe() {
        return "использует Flash Cannon, сносит 1 спец защиту";
    }
    @Override
    protected void applyOppEffects(Pokemon p) {
        Effect e = new Effect().chance(0.1).stat(Stat.SPECIAL_DEFENSE, -1);
        p.addEffect(e);
    }
}
