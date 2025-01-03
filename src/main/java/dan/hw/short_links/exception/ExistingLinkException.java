package dan.hw.short_links.exception;

public class ExistingLinkException extends Exception {

    public ExistingLinkException(String userName, String orinLink) {
        super(String.format("Для пользователя '%s' еще действует ссылка '%s'", userName, orinLink));
    }
}
