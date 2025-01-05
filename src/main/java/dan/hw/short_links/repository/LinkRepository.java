package dan.hw.short_links.repository;

import dan.hw.short_links.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {

    @Query("""
        SELECT l FROM Link l
        JOIN l.linkMaster lm
        WHERE lm.id = :linkMasterId
          AND l.shortLink = :shortLink
          AND l.active is true;
    """)
    List<Link> findActiveLinkByUserAndShortLink(
            @Param("linkMasterId") String linkMasterId,
            @Param("shortLink") String shortLink);

    @Query("""
        SELECT l FROM Link l
        JOIN l.linkMaster lm
        WHERE lm.id = :linkMasterId
          AND l.shortLink = :shortLink
    """)
    List<Link> findAllByUserAndShortLink(
            @Param("linkMasterId") String linkMasterId,
            @Param("shortLink") String shortLink);

    @Query("""
                SELECT l FROM Link l
                WHERE l.active = true AND (l.remainder <= 0 OR l.toDate <= CURRENT_TIMESTAMP)
            """)
    List<Link> findForDeactivate();
}