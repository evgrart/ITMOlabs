package client.commands;

import general.ClientCommand;
import general.Request;
import general.exceptions.InvalidInputException;
import general.interfaces.Initialization;
import general.interfaces.RequestCreator;
import general.interfaces.ValidatableCommand;
import client.input_managers.InputManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Выполнений скрипт, расположенный по указанному пути (перенаправляем файл в стандартный ввод)
 */
public class ExecuteScript extends ClientCommand implements Initialization<ArrayList<Request<ClientCommand, ?>>>, ValidatableCommand, RequestCreator {
    private final String FILE_NAME;
    private ArrayList<Request<ClientCommand, ?>> list = new ArrayList<>();

    public ExecuteScript(String parameter) {
        super(parameter);
        this.FILE_NAME = this.parameter;
    }

    @Override
    public boolean validate() {
        try {
            if (this.parameter != null) {
                if (!new File(FILE_NAME).exists()) {
                    throw new InvalidInputException("Ошибка: файл не существует\n");
                }
                return true;
            } else {
                throw new InvalidInputException("У execute_script должен быть аргумент: путь до файла!\n");
            }
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public Request toRequest() {
        Request<String, ArrayList<Request<ClientCommand, ?>>> request = new Request<>(this.toString(), this.initialize());
        return request;
    }

    @Override
    public String toString() {
        return "execute_script";
    }

    public ArrayList<Request<ClientCommand, ?>> initialize() {
        try {
            /*
            Команда перенаправляет стандартный ввод на чтение данных из файла вместо консоли
            new FileInputStream(FILE_NAME) создает поток для чтения данных из указанного файла.
            System.setIn(...) перенаправляет ввод так, что System.in теперь читает не с клавиатуры, а из файла.
             */
            System.setIn(new FileInputStream(FILE_NAME));
            InputManager.consoleRead = new Scanner(System.in);
            ClientCommand command = InputManager.startInput();
            list.add(command.toRequest());
            return list;

        } catch (IOException e) {
            throw new InvalidInputException("Ошибка при чтении файла: " + e.getMessage() + "\nПерепроверьте, существует ли такой файл.\n");
        } finally {
            return list;
        }
    }
}
