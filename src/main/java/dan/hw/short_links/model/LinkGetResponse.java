package dan.hw.short_links.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Модель для передачи данных при выводе информации по ссылке.
 */
@Data
@AllArgsConstructor
public class LinkGetResponse {

    private String message;
    private String origLink;
}
