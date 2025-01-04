package dan.hw.short_links.repository;

import dan.hw.short_links.entity.Links;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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
    """)
    List<Links> findAllByUserAndShortLink(
            @Param("userName") String userName,
            @Param("shortLink") String shortLink);

    @Modifying
    @Query("""
            UPDATE Links l
            SET l.active = false
            WHERE l.active = true AND (l.remainder <= 0 OR l.toDate <= CURRENT_TIMESTAMP)
            """)
    int deactivateExpiredLinks();

    @Query("""
        SELECT l FROM Links l
        WHERE l.active = true AND (l.remainder <= 0 OR l.toDate <= CURRENT_TIMESTAMP)
    """)
    List<Links> findForDeactivate();
}