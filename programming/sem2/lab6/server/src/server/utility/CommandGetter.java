package server.utility;

import server.commands.*;

import java.util.HashMap;

public class CommandGetter {
    public HashMap<String, Command> commands = new HashMap<>();

    public CommandGetter() {
        commands.put("command", new Command());
        commands.put("help", new Help());
        commands.put("info", new Info());
        commands.put("show", new Show());
        commands.put("add", new Add());
        commands.put("update", new Update());
        commands.put("remove_by_id", new RemoveById());
        commands.put("clear", new Clear());
        commands.put("execute_script", new ExecuteScript());
        commands.put("remove_head", new RemoveHead());
        commands.put("path_setter", new PathSetter());
        commands.put("remove_lower", new RemoveLower());
        commands.put("history", new History());
        commands.put("remove_all_by_group_admin", new RemoveAllByGroupAdmin());
        commands.put("count_greater_than_group_admin", new CountGreaterThanGroupAdmin());
        commands.put("filter_greater_than_form_of_education", new FilterGreaterThanFormOfEducation());
    }

    public Command getCommand(String command) {
        return commands.get(command);
    }
}
