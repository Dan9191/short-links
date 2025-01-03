package dan.hw.short_links.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LinkRequest {

    private String userName;
    private String origLink;
    private String toDate;
    private Long remainder;
}
