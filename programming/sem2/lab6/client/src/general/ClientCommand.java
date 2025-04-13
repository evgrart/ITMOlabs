package general;

import general.interfaces.RequestCreator;
import general.interfaces.ValidatableCommand;

import java.io.Serializable;

/**
 * Класс-предок для всех команд пакета commands, тут обрабатываются всё, что ожидалось как команда, но среди команд этого нет
 */
public class ClientCommand implements ValidatableCommand, Serializable, RequestCreator {
    /**
     * Параметр команды: для update это id, для add - element и тд. Вообще-м обязательная часть синтаксиса какой-то команды
     * <p></p>
     * Если у команды нет параметра, то необходимо отправить в конструктор null
     */
    public String parameter;

    public ClientCommand(String parameter) {
        this.parameter = parameter;
    }

    public boolean validate() {
        return true;
    }

    @Override
    public Request toRequest() {
        Request<String, String> request = new Request<>(this.toString(), "Такой команды нет! Используйте команду help, чтобы посмотреть список команд\n");
        return request;
    }

    @Override
    public String toString() {
        return "command";
    }

}
