package dan.hw.short_links.model;

import dan.hw.short_links.entity.Link;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LinkEdit {

    private String linkMasterId;
    private String shortLink;
    private String toDate;
    private Long remainder;
    private StatusStub statusStub;

    public LinkEdit(Link link) {
        this.linkMasterId = link.getLinkMaster().getId();
        this.shortLink = link.getShortLink();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.toDate = link.getToDate().format(formatter);
        this.remainder = link.getRemainder();
        this.statusStub = StatusStub.getStatusStub(link.isActive());
    }
}
