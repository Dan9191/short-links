package dan.hw.short_links.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Модель для передачи данных при получении ссылки.
 */
@Data
@AllArgsConstructor
public class LinkGet {

    private String linkMasterId;
    private String shortLink;
}
