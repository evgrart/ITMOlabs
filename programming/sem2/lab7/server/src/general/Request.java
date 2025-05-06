//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package general;

import java.io.Serializable;

public class Request<String, V> implements Serializable {
    private static final long serialVersionUID = 123456789L;
    String cmd;
    V arg;
    String login;

    public Request(String cmd, V arg, String login) {
        this.cmd = (String)cmd;
        this.arg = arg;
        this.login = login;
    }

    public String getCmd() {
        return this.cmd;
    }

    public V getArg() {
        return this.arg;
    }

    public String getLogin() {
        return this.login;
    }
}
