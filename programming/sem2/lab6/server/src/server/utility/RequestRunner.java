package server.utility;

import general.Request;
import general.ClientCommand;
import general.exceptions.InvalidInputException;


public class RequestRunner {
    private static CommandGetter getter = new CommandGetter();

    public static String run(Request<String, ?> request) {

        try {
            if (request == null) {
                return null;
            }
            String cmd = request.getCmd();
            Object argument = request.getArg();
            return getter.getCommand(cmd).execute(argument);
        } catch (InvalidInputException e) {
            return e.getMessage();
        }

    }
}
