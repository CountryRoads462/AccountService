package account;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p WHERE p.username = ?1 AND p.period = ?2")
    Payment findPaymentByUsernameAndPeriod(String username, LocalDate period);

    @Query("SELECT p FROM Payment p WHERE p.username = ?1 ORDER BY p.period DESC")
    List<Payment> findPaymentsByUsernameOrderByPeriodDesc(String username);

}
