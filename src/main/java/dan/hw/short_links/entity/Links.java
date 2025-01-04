package dan.hw.short_links.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "links")
@Data
public class Links {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "link_master_id", nullable = false)
    private LinkMaster linkMaster;

    @Column(name = "orig_link", nullable = false)
    private String origLink;

    @Column(name = "short_link", nullable = false)
    private String shortLink;

    @Column(name = "from_date", columnDefinition = "TIMESTAMP DEFAULT NOW()")
    private LocalDateTime fromDate;

    @Column(name = "to_date")
    private LocalDateTime toDate;

    @Column(name = "remainder")
    private Long remainder;
}
