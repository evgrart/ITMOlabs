//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package server.commands;

import general.User;
import general.exceptions.InvalidInputException;
import java.sql.SQLException;
import java.util.List;
import server.interfaces.Executable;
import server.interfaces.ValidatableParameter;
import server.utility.RequestRunner;

public class Password extends Command implements ValidatableParameter<List<String>>, Executable {
    private List<String> list;
    private User user;
    private String login;
    private String password;
    private String type;

    public Password() {
    }

    public List<String> validate(Object parameter) {
        if (parameter instanceof List<?>) {
            return (List<String>) parameter;
        } else {
            throw new InvalidInputException("Parameter of password command must be a List<String>");
        }
    }

    public synchronized String execute(Object parameter, String login) {
        try {
            this.login = login;
            this.list = this.validate(parameter);
            login = this.list.get(0);
            this.password = this.list.get(1);
            this.type = this.list.get(2);
            this.user = new User(login, this.password);
            return RequestRunner.dbmanager.checkUser(this.user, this.type);
        } catch (SQLException | InvalidInputException e) {
            return e.getMessage();
        }
    }
}
