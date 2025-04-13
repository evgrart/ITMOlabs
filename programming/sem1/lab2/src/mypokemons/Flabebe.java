//https://pokemondb.net/pokedex/flabebe
package mypokemons;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;
import mymoves.flabebe.*;

public class Flabebe extends Pokemon {
    public Flabebe(String name, int level) {
        super(name, level);
        setType(Type.FAIRY);
        setStats(44, 38, 39, 61, 79, 42);
        setMove(new EnergyBall(), new PetalBlizzard());
    }
}
