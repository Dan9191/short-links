package dan.hw.short_links.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LinkRequest {

    private String userName;
    private String origLink;
    private String toDate;
    private Long remainder;
}
