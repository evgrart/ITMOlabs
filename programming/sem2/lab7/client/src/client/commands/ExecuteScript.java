package client.commands;

import client.Client;
import client.input_managers.InputManager;
import general.ClientCommand;
import general.Request;
import general.exceptions.InvalidInputException;
import general.interfaces.Initialization;
import general.interfaces.RequestCreator;
import general.interfaces.ValidatableCommand;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ExecuteScript extends ClientCommand implements Initialization<ArrayList<Request<ClientCommand, ?>>>, ValidatableCommand, RequestCreator {
    private final String FILE_NAME;
    private ArrayList<Request<ClientCommand, ?>> list = new ArrayList();

    public ExecuteScript(String parameter) {
        super(parameter);
        this.FILE_NAME = this.parameter;
    }

    public boolean validate() {
        try {
            if (this.parameter != null) {
                if (!(new File(this.FILE_NAME)).exists()) {
                    throw new InvalidInputException("Ошибка: файл не существует\n");
                } else {
                    return true;
                }
            } else {
                throw new InvalidInputException("У execute_script должен быть аргумент: путь до файла!\n");
            }
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public Request toRequest() {
        Request<String, ArrayList<Request<ClientCommand, ?>>> request = new Request(this.toString(), this.initialize(), Client.login);
        return request;
    }

    public String toString() {
        return "execute_script";
    }

    public ArrayList<Request<ClientCommand, ?>> initialize() {
        try {
            System.setIn(new FileInputStream(this.FILE_NAME));
            InputManager.consoleRead = new Scanner(System.in);
            ClientCommand command = InputManager.startInput();
            this.list.add(command.toRequest());
        } catch (IOException e) {
            throw new InvalidInputException("Ошибка при чтении файла: " + e.getMessage() + "\nПерепроверьте, существует ли такой файл.\n");
        } finally {
            return this.list;
        }
    }
}
