package dan.hw.short_links.repository;

import dan.hw.short_links.entity.LinkMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LinkMasterRepository extends JpaRepository<LinkMaster, UUID> {

    Optional<LinkMaster> findByName(String linkMasterName);
}