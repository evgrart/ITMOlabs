package interfaces;

/**
 * Интерфейс, добавляет ко всем командам метод execute() - выполнение команды.
 * <p></p>
 * (на самом деле бесполезный, ибо реализуется в предке всех команд {@link commands.Command})
 */
public interface Executable {
    public void execute();
}
    