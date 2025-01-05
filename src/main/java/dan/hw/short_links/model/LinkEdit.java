package dan.hw.short_links.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LinkEdit {

    private String linkMasterId;
    private String shortLink;
    private String toDate;
    private Long remainder;
    private StatusStub statusStub;
}
