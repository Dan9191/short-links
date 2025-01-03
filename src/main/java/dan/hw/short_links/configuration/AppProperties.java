package dan.hw.short_links.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "short-links.properties")
@Getter
@Setter
public class AppProperties {
    private String unit;
    private int amountToAdd;

}
