package dan.hw.short_links.exception;

/**
 * Ошибка преобразования даты.
 */
public class IncorrectDateException extends Exception {

    public IncorrectDateException(String message) {
        super(message);
    }
}
