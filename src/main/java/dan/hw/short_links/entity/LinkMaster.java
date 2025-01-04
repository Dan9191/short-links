package dan.hw.short_links.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "link_master")
@Data
public class LinkMaster {

    @Id
    @GeneratedValue(generator = "uuid")
    private UUID id;

    @Column
    private String name;

}
