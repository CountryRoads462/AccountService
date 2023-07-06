package account;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class PaymentDuplicateException extends RuntimeException {

    public PaymentDuplicateException(String message) {
        super(message);
    }
}
