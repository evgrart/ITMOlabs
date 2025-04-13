//https://pokemondb.net/pokedex/croagunk
package mypokemons;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;
import mymoves.croagunk.*;

public class Croagunk extends Pokemon {
    public Croagunk(String name, int level) {
        super(name, level);
        setType(Type.POISON, Type.FIGHTING);
        setStats(48, 61, 40, 61, 40, 50);
        setMove(new DarkPulse(), new SludgeBomb(), new DoubleTeam());
    }
}
