//https://pokemondb.net/pokedex/toxicroak
package mypokemons;
import ru.ifmo.se.pokemon.Type;
import mymoves.toxicroak.*;

public final class Toxicroak extends Croagunk{
    public Toxicroak(String name, int level) {
        super(name, level);
        setType(Type.POISON, Type.FIGHTING);
        setStats(83, 106, 65, 86, 65, 85);
        setMove(new StoneEdge());
    }
}
