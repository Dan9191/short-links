package dan.hw.short_links.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

/**
 * Сущность для хранения ссылки.
 */
@Entity
@Table(name = "short_link")
@Data
public class Link {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
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

    @Column(name = "active")
    private boolean active;
}
