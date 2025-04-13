//https://pokemondb.net/pokedex/floette
package mypokemons;
import ru.ifmo.se.pokemon.Type;
import mymoves.floette.*;

public class Floette extends Flabebe {
    public Floette(String name, int level) {
        super(name, level);
        setType(Type.FAIRY);
        setStats(54, 45, 47, 75, 98, 52);
        setMove(new RazorLeaf());
    }
}
