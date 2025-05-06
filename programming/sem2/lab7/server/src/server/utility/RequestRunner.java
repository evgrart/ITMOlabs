//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package server.utility;

import general.Request;
import general.exceptions.InvalidInputException;

public class RequestRunner {
    private static CommandGetter getter = new CommandGetter();
    public static DBmanager dbmanager = new DBmanager();

    public RequestRunner() {
    }

    public String run(Request<String, ?> request) {
        try {
            if (request == null) {
                return null;
            } else {
                String cmd = request.getCmd();
                String login = request.getLogin();
                Object argument = request.getArg();
                return getter.getCommand(cmd).execute(argument, login);
            }
        } catch (InvalidInputException e) {
            return e.getMessage();
        }
    }
}
