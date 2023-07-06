package account;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BreachedPasswordsRepository extends CrudRepository<BreachedPassword, Long> {

    boolean existsByPassword(String password);
}
