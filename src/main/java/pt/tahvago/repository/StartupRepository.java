package pt.tahvago.repository;

import java.util.List;
import java.util.Optional; // Add this import

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pt.tahvago.model.Startup;

@Repository
public interface StartupRepository extends JpaRepository<Startup, Long> {

    List<Startup> findAllByOwnerId(Long ownerId);

    boolean existsByOwnerId(Long ownerId);

    @Query("SELECT s FROM Startup s JOIN FETCH s.owner WHERE s.id = :id")
    Optional<Startup> findByIdWithFullDetails(@Param("id") Long id);

} 