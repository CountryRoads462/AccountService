package account;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Locale;

public class CustomException extends RuntimeException {

    private LinkedHashMap<String, Object> errorBody;
    private HttpStatus status;

    public CustomException() {
    }

    public CustomException(String message, HttpStatus httpStatus) {
        LinkedHashMap<String, Object> errorBody = new LinkedHashMap<>();
        errorBody.put("timestamp", LocalDateTime.now());
        errorBody.put("status", httpStatus.value());
        errorBody.put("error", httpStatus.getReasonPhrase());
        errorBody.put("message", message);

        this.status = httpStatus;
        this.errorBody = errorBody;
    }


    public LinkedHashMap<String, Object> getErrorBody() {
        return errorBody;
    }

    public HttpStatus getStatus() {
        return status;
    }

}

