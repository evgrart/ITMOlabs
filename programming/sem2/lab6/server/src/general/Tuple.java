package general;

import java.io.Serializable;

public class Tuple<A, B> implements Serializable {
    private final A first;
    private final B second;

    public Tuple(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public B getSecond() {
        return second;
    }

    public A getFirst() {
        return first;
    }
}
