package pt.tahvago.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pt.tahvago.model.Conference;

@Repository
public interface ConferenceRepository extends CrudRepository<Conference, Long> {
}