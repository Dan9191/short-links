package dan.hw.short_links.exception;

/**
 * Ошибка поиска активных ссылок.
 */
public class NotActiveLinkException extends Exception {

    public NotActiveLinkException(String userName, String orinLink) {
        super(String.format("Для пользователя '%s' не найдена активная ссылка '%s'", userName, orinLink));
    }
}
