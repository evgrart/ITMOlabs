package client.input_managers;

import client.Main;
import client.commands.*;
import general.ClientCommand;

import java.util.HashMap;

/**
 * Класс, отвечающий за выполнение команд
 */
public class CommandManager {
    public HashMap<String, ClientCommand> commands = new HashMap<>();

    public CommandManager(String parameter) {
        commands.put("help", new Help(parameter));
        commands.put("info", new Info(parameter));
        commands.put("show", new Show(parameter));
        commands.put("add", new Add(parameter));
        commands.put("update", new Update(parameter));
        commands.put("remove_by_id", new RemoveById(parameter));
        commands.put("clear", new Clear(parameter));
        commands.put("execute_script", new ExecuteScript(parameter));
        commands.put("remove_head", new RemoveHead(parameter));
        commands.put("remove_lower", new RemoveLower(parameter));
        commands.put("path_getter", new PathSetter(parameter, Main.file));
        commands.put("history", new History(parameter));
        commands.put("remove_all_by_group_admin", new RemoveAllByGroupAdmin(parameter));
        commands.put("count_greater_than_group_admin", new CountGreaterThanGroupAdmin(parameter));
        commands.put("filter_greater_than_form_of_education", new FilterGreaterThanFormOfEducation(parameter));
    }

    /**
     * Если команда найдена, то дальше проверяется на валидность и проверяется, если нет, то вызывается предок {@link ClientCommand}
     */
    public ClientCommand runCommand(String cm) {
        if (commands.containsKey(cm)) {
            ClientCommand command = commands.get(cm);
            if (command.validate()) {
                return command;
            } else {
                InputManager.startInput();
            }
        } else {
            return new ClientCommand(null);
        }
        return null;
    }
}