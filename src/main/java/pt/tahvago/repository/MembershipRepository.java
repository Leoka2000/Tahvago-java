package pt.tahvago.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pt.tahvago.model.Membership;

@Repository
public interface MembershipRepository extends CrudRepository<Membership, Long> {
    Optional<Membership> findByUserId(Long userId);
    
}