package dan.hw.short_links.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Модель для передачи данных при редактировании ссылки.
 */
@Component
@ConfigurationProperties(prefix = "short-links.properties")
@Getter
@Setter
public class AppProperties {

    /**
     * Мера измерения для вычисления времени действия ссылки.
     */
    private String unit;

    /**
     * Количественное представление времени действия ссылки.
     */
    private int amountToAdd;

    /**
     * Количество переходов по ссылке.
     */
    private Long remainder;

}
