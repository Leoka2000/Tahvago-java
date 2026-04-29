package pt.tahvago.repository;

import java.util.List;
import java.util.Optional; // Add this import

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import pt.tahvago.model.Startup;

@Repository
public interface StartupRepository extends JpaRepository<Startup, Long> {

    List<Startup> findAllByOwnerId(Long ownerId);

    boolean existsByOwnerId(Long ownerId);

    Optional<Startup> findById(Long id);


}