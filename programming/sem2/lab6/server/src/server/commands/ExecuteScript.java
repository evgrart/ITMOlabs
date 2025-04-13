package server.commands;

import general.ClientCommand;
import general.Request;
import general.exceptions.InvalidInputException;

import server.interfaces.Executable;
import server.interfaces.ValidatableParameter;
import server.utility.CollectionManager;
import server.utility.HistoryParser;
import server.utility.RequestRunner;

import java.util.ArrayList;


/**
 * Выполнений скрипт, расположенный по указанному пути (перенаправляем файл в стандартный ввод)
 */
public class ExecuteScript extends Command implements ValidatableParameter<ArrayList<Request<String, ?>>>, Executable {
    private ArrayList<Request<String, ?>> requests;

    @Override
    public ArrayList<Request<String, ?>> validate(Object parameter) {
        try {
            Request<String, ?> test = ((ArrayList<Request<String, ?>>) parameter).get(0);
            return ((ArrayList<Request<String, ?>>) parameter);
        } catch (ClassCastException e) {
            throw new InvalidInputException("Parameter of add command must be a StudyGroup.StudyGroupBuilder");
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidInputException("File is empty or file doesn't exist!");
        }
    }

    @Override
    public String execute(Object parameter) {
        try {
            requests = validate(parameter);
            CollectionManager.commandsList.add("remove_by_id");
            HistoryParser.parseToFile();

            if (requests.isEmpty()) {
                return "Nothing to execute";
            }

            for (Request<String, ?> request : requests) {
                sb.append(RequestRunner.run(request));
            }

            return sb.toString();

        } catch (StackOverflowError e) {
            return "Ай-ай-ай, рекурсия!";
        }
    }
}