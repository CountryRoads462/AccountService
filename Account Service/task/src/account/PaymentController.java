package account;

import jakarta.transaction.Transactional;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorManager;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorManagerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@Validated
public class PaymentController {

    @Autowired
    UserRepository userRepo;

    @Autowired
    PaymentRepository paymentRepo;

    @Transactional
    @PostMapping(path = "api/acct/payments")
    public Map<String, String> uploadsPayrolls(@RequestBody List<@Valid PaymentRequestBody> payments) {
        for (PaymentRequestBody paymentRequestBody :
                payments) {
            String username = paymentRequestBody.getEmployee();
            LocalDate period = LocalDatePeriodParser.parse(paymentRequestBody.getPeriod());

            if (paymentRepo.findPaymentByUsernameAndPeriod(username, period) != null) {
                throw new PaymentDuplicateException("error!");
            }

            long salary = paymentRequestBody.getSalary();

            Payment payment = new Payment(username, period, salary);

            paymentRepo.save(payment);
        }

        return new HashMap<>(Map.of("status", "Added successfully!"));
    }

    @PutMapping(path = "api/acct/payments")
    public Map<String, String> changeTheSalary(@Valid @RequestBody PaymentRequestBody paymentRequestBody) {
        String username = paymentRequestBody.getEmployee();
        LocalDate period = LocalDatePeriodParser.parse(paymentRequestBody.getPeriod());

        Payment payment = paymentRepo.findPaymentByUsernameAndPeriod(username, period);

        long newSalary = paymentRequestBody.getSalary();
        payment.setSalary(newSalary);

        paymentRepo.save(payment);

        return new HashMap<>(Map.of("status", "Updated successfully!"));
    }

    @GetMapping(path = "api/empl/payment")
    public Object giveEmployeesPayrolls(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "period", required = false)
            @Pattern(regexp = "((0\\d)|(1[0-2]))-\\d{4}") String periodParam
    ) {
        User user = userRepo.findByUsername(userDetails.getUsername()).orElse(null);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        List<ChangeTheSalaryResponseForm> giveEmployeesPayrollsResponse = new ArrayList<>();

        String name = user.getName();
        String lastname = user.getLastname();
        if (periodParam != null) {
            LocalDate period = LocalDatePeriodParser.parse(periodParam);

            Payment payment = paymentRepo.findPaymentByUsernameAndPeriod(
                    user.getUsername(),
                    period
            );

            if (payment == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            } else {
                return new ChangeTheSalaryResponseForm(
                        name,
                        lastname,
                        period,
                        payment.getSalary()
                );
            }
        }

        paymentRepo.findPaymentsByUsernameOrderByPeriodDesc(user.getUsername())
                .forEach(payment -> {
                    giveEmployeesPayrollsResponse.add(new ChangeTheSalaryResponseForm(
                            name,
                            lastname,
                            payment.getPeriod(),
                            payment.getSalary()
                    ));
                });

        return giveEmployeesPayrollsResponse;
    }

}
