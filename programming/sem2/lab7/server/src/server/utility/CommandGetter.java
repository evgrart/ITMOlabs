//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package server.utility;

import java.util.HashMap;
import server.commands.Add;
import server.commands.Clear;
import server.commands.Command;
import server.commands.CountGreaterThanGroupAdmin;
import server.commands.ExecuteScript;
import server.commands.FilterGreaterThanFormOfEducation;
import server.commands.Help;
import server.commands.History;
import server.commands.Info;
import server.commands.Password;
import server.commands.RemoveAllByGroupAdmin;
import server.commands.RemoveById;
import server.commands.RemoveHead;
import server.commands.RemoveLower;
import server.commands.Show;
import server.commands.Update;

public class CommandGetter {
    public HashMap<String, Command> commands = new HashMap();

    public CommandGetter() {
        this.commands.put("command", new Command());
        this.commands.put("help", new Help());
        this.commands.put("info", new Info());
        this.commands.put("show", new Show());
        this.commands.put("add", new Add());
        this.commands.put("update", new Update());
        this.commands.put("remove_by_id", new RemoveById());
        this.commands.put("clear", new Clear());
        this.commands.put("execute_script", new ExecuteScript());
        this.commands.put("password", new Password());
        this.commands.put("remove_head", new RemoveHead());
        this.commands.put("remove_lower", new RemoveLower());
        this.commands.put("history", new History());
        this.commands.put("remove_all_by_group_admin", new RemoveAllByGroupAdmin());
        this.commands.put("count_greater_than_group_admin", new CountGreaterThanGroupAdmin());
        this.commands.put("filter_greater_than_form_of_education", new FilterGreaterThanFormOfEducation());
    }

    public Command getCommand(String command) {
        return this.commands.get(command);
    }
}
