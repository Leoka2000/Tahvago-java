package pt.tahvago.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pt.tahvago.model.Startup;

@Repository
public interface StartupRepository extends JpaRepository<Startup, Long> {
    Optional<Startup> findByOwnerId(Long userId);
}