package client.input_managers;

import client.commands.Add;
import client.commands.Clear;
import client.commands.CountGreaterThanGroupAdmin;
import client.commands.ExecuteScript;
import client.commands.FilterGreaterThanFormOfEducation;
import client.commands.Help;
import client.commands.History;
import client.commands.Info;
import client.commands.RemoveAllByGroupAdmin;
import client.commands.RemoveById;
import client.commands.RemoveHead;
import client.commands.RemoveLower;
import client.commands.Show;
import client.commands.Update;
import general.ClientCommand;
import java.util.HashMap;

public class CommandManager {
    public HashMap<String, ClientCommand> commands = new HashMap();

    public CommandManager(String parameter) {
        this.commands.put("help", new Help(parameter));
        this.commands.put("info", new Info(parameter));
        this.commands.put("show", new Show(parameter));
        this.commands.put("add", new Add(parameter));
        this.commands.put("update", new Update(parameter));
        this.commands.put("remove_by_id", new RemoveById(parameter));
        this.commands.put("clear", new Clear(parameter));
        this.commands.put("execute_script", new ExecuteScript(parameter));
        this.commands.put("remove_head", new RemoveHead(parameter));
        this.commands.put("remove_lower", new RemoveLower(parameter));
        this.commands.put("history", new History(parameter));
        this.commands.put("remove_all_by_group_admin", new RemoveAllByGroupAdmin(parameter));
        this.commands.put("count_greater_than_group_admin", new CountGreaterThanGroupAdmin(parameter));
        this.commands.put("filter_greater_than_form_of_education", new FilterGreaterThanFormOfEducation(parameter));
    }

    public ClientCommand runCommand(String cm) {
        if (this.commands.containsKey(cm)) {
            ClientCommand command = this.commands.get(cm);
            if (command.validate()) {
                return command;
            } else {
                InputManager.startInput();
                return null;
            }
        } else {
            return new ClientCommand(null);
        }
    }
}
