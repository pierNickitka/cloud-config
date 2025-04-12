package ag.selmag.manager.repository;

import ag.selmag.manager.entity.SelmagUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface SelmagUserRepository extends CrudRepository<SelmagUser, Integer> {

  Optional<SelmagUser> findByUsername(String username);
}
