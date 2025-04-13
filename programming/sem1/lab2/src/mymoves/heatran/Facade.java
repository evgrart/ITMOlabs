//https://pokemondb.net/move/facade
package mymoves.heatran;
import ru.ifmo.se.pokemon.*;

public final class Facade extends PhysicalMove {
    public Facade() {
        super(Type.NORMAL, 70, 100);
    }

    private boolean flag = false;

    @Override
    protected void applySelfEffects(Pokemon p) {
        Status condition = p.getCondition();
        if (condition == Status.BURN | condition == Status.PARALYZE | condition == Status.POISON){
            flag = true;
            this.power *= 2;
        }
    }

    @Override
    protected String describe(){
        if (flag) return "Покемон использует Facade с удвоенным уроном";
        else return "использует атаку Facade";
    }
}
