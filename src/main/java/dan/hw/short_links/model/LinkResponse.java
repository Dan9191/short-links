package dan.hw.short_links.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LinkResponse {

    private String linkMasterId;
    private String shortLink;
}
