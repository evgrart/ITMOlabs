package general;

import java.io.Serializable;

public class Request<String, V> implements Serializable {
    String cmd;
    V arg;

    public Request(java.lang.String cmd, V arg) {
        this.cmd = (String) cmd;
        this.arg = arg;
    }

    public java.lang.String getCmd() {
        return (java.lang.String) cmd;
    }

    public V getArg() {
        return arg;
    }
}
