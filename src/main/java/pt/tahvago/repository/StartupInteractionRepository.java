package pt.tahvago.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pt.tahvago.model.StartupInteraction;
import java.util.Optional;

@Repository
public interface StartupInteractionRepository extends JpaRepository<StartupInteraction, Long> {

    @Query("SELECT i FROM StartupInteraction i " +
           "JOIN FETCH i.sender s " +
           "JOIN FETCH s.owner " +
           "JOIN FETCH i.receiver r " +
           "JOIN FETCH r.owner " +
           "WHERE i.id = :id")
    Optional<StartupInteraction> findByIdWithFullDetails(@Param("id") Long id);
}