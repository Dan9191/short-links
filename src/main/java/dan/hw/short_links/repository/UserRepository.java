package dan.hw.short_links.repository;

import dan.hw.short_links.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @Query("""
        SELECT u FROM user u
        WHERE u.name = :userName
    """)
    Optional<User> byName(@Param("userName") String userName);
}