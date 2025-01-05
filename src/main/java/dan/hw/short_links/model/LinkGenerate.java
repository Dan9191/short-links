package dan.hw.short_links.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Модель для передачи данных при генерации ссылки.
 */
@Data
@AllArgsConstructor
public class LinkGenerate {

    private String userName;
    private String origLink;
    private String toDate;
    private Long remainder;
}
