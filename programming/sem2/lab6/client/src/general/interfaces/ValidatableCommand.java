package general.interfaces;

/**
 * Интерфейс, добавляет ко всем командам метод validate() - проверку на валидность команды (верно ли её ввел юзер).
 * <p></p>
 * (на самом деле бесполезный, ибо реализуется в предке всех команд {@link general.ClientCommand})
 */
public interface ValidatableCommand {
    public boolean validate();
}