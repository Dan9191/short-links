package dan.hw.short_links.repository;

import dan.hw.short_links.entity.Links;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LinksRepository extends JpaRepository<Links, Long> {

    @Query("""
        SELECT l FROM Links l
        JOIN l.linkMaster lm
        WHERE lm.name = :userName
          AND l.origLink = :origLink
          AND l.remainder > 0
          AND l.toDate > CURRENT_TIMESTAMP
    """)
    Optional<Links> findActiveLinkByUserAndOrigLink(
            @Param("userName") String userName,
            @Param("origLink") String origLink);

    @Query("""
        SELECT l FROM Links l
        JOIN l.linkMaster lm
        WHERE lm.name = :userName
          AND l.shortLink = :shortLink
          AND l.remainder > 0
          AND l.toDate > CURRENT_TIMESTAMP
    """)
    Optional<Links> findActiveLinkByUserAndShortLink(
            @Param("userName") String userName,
            @Param("shortLink") String shortLink);
}