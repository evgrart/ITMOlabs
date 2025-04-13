package exceptions;

/**
 * Вызываем исключение, если команда/поле введенное юзером не валидно по ТЗ и предлагаем ввести снова
 */
public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) {
        super(message);
    }
}
