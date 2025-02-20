package dan.hw.short_links.exception;

/**
 * Ошибка поиска ссылок.
 */
public class NotExistingLinkException extends Exception {

    public NotExistingLinkException(String userName, String orinLink) {
        super(String.format("Для пользователя '%s' не найдена ссылка '%s'", userName, orinLink));
    }
}
