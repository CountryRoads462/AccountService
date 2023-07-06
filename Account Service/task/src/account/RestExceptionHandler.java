package account;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({CustomException.class})
    public ResponseEntity<Object> handleCustomException(
            CustomException e,
            WebRequest request
    ) {
        LinkedHashMap<String, Object> body = e.getErrorBody();
        body.put("path", request.getDescription(false).replaceFirst("uri=", ""));
        return new ResponseEntity<>(e.getErrorBody(), e.getStatus());
    }

    @ExceptionHandler({PaymentDuplicateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handlePaymentDuplicateException(
            PaymentDuplicateException e,
            WebRequest request
    ) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();

        map.put("timestamp", LocalDateTime.now());
        map.put("status", 400);
        map.put("error", "Bad Request");
        map.put("message", e.getMessage());
        map.put("path", request.getDescription(false).replaceFirst("uri=", ""));

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleConstraintViolationException(
            ConstraintViolationException e,
            WebRequest request
    ) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();

        map.put("timestamp", LocalDateTime.now());
        map.put("status", 400);
        map.put("error", "Bad Request");
        map.put("message", e.getMessage());
        map.put("path", request.getDescription(false).replaceFirst("uri=", ""));

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }



    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();

        map.put("timestamp", LocalDateTime.now());
        map.put("status", 400);
        map.put("error", "Bad Request");
        map.put("message", ex.getMessage());
        map.put("path", request);

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

}