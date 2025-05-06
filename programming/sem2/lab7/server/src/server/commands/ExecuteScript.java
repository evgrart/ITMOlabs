//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package server.commands;

import general.Request;
import general.exceptions.InvalidInputException;
import java.util.ArrayList;
import server.interfaces.Executable;
import server.interfaces.ValidatableParameter;
import server.utility.HistoryParser;
import server.utility.RequestRunner;

public class ExecuteScript extends Command implements ValidatableParameter<ArrayList<Request<String, ?>>>, Executable {
    private ArrayList<Request<String, ?>> requests;
    private RequestRunner runner;
    private String login;

    public ExecuteScript() {
    }

    public ArrayList<Request<String, ?>> validate(Object parameter) {
        try {
            Request<String, ?> test = (Request)((ArrayList)parameter).get(0);
            return (ArrayList)parameter;
        } catch (ClassCastException var3) {
            throw new InvalidInputException("Parameter of add command must be a StudyGroup.StudyGroupBuilder");
        } catch (IndexOutOfBoundsException var4) {
            throw new InvalidInputException("File is empty or file doesn't exist!");
        }
    }

    public synchronized String execute(Object parameter, String login) {
        try {
            this.login = login;
            this.requests = this.validate(parameter);
            HistoryParser.commandsList.add("remove_by_id");
            HistoryParser.parseToFile();
            if (this.requests.isEmpty()) {
                return "Nothing to execute";
            } else {
                for(Request<String, ?> request : this.requests) {
                    this.sb.append(this.runner.run(request));
                }

                return this.sb.toString();
            }
        } catch (StackOverflowError var5) {
            return "Ай-ай-ай, рекурсия!";
        }
    }
}
