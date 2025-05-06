package client.commands;

import client.Client;
import client.input_managers.InputManager;
import general.ClientCommand;
import general.Request;
import general.interfaces.RequestCreator;
import java.util.ArrayList;
import java.util.List;

public class Password extends ClientCommand implements RequestCreator {
    public Password(String parameter) {
        super(parameter);
    }

    public Request toRequest() {
        Request<String, List<String>> request = new Request(this.toString(), this.initialize(), Client.login);
        return request;
    }

    public List<String> initialize() {
        String line;
        do {
            System.out.print("Please select, login or register new user: ");
            line = InputManager.consoleRead.nextLine().strip();
        } while(!line.equals("register") && !line.equals("login"));

        List<String> list = new ArrayList();
        System.out.print("Enter login: ");
        String login = InputManager.consoleRead.nextLine().strip();
        System.out.print("Enter password: ");
        String password = InputManager.consoleRead.nextLine().strip();
        list.add(login);
        list.add(password);
        list.add(line);
        Client.login = login;
        System.out.println();
        return list;
    }

    public String toString() {
        return "password";
    }
}
