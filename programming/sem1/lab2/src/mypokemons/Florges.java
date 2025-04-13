//https://pokemondb.net/pokedex/florges
package mypokemons;
import ru.ifmo.se.pokemon.Type;
import mymoves.florges.*;

public final class Florges extends Floette {
    public Florges(String name, int level) {
        super(name, level);
        setType(Type.FAIRY);
        setStats(78, 65, 68, 112, 154, 75);
        setMove(new Psychic());
    }
}
