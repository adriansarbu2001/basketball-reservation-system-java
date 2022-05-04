package basketball.network.dto;

import java.io.Serializable;

public class Pair<E1, E2> implements Serializable {
    E1 first;
    E2 second;

    public Pair(E1 e1, E2 e2) {
        this.first = e1;
        this.second = e2;
    }

    public E1 getFirst() {
        return first;
    }

    public void setFirst(E1 first) {
        this.first = first;
    }

    public E2 getSecond() {
        return second;
    }

    public void setSecond(E2 second) {
        this.second = second;
    }
}
