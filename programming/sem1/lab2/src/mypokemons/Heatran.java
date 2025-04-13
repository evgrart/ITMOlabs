//https://pokemondb.net/pokedex/heatran
package mypokemons;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;
import mymoves.heatran.*;

public final class Heatran extends Pokemon {
    public Heatran(String name, int level) {
        super(name, level);
        setType(Type.FIRE, Type.STEEL);
        setStats(91, 90, 106, 130, 106, 77);
        setMove(new FlashCannon(), new RockSlide(), new RockTomb(), new Facade());
    }
}
