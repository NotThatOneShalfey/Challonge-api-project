package Challonge.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, code = HttpStatus.NOT_FOUND)
public class RequestResponseNotFoundException extends Exception{
    public RequestResponseNotFoundException() {
    }

    public RequestResponseNotFoundException(String message) {
        super(message);
    }

    public RequestResponseNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestResponseNotFoundException(Throwable cause) {
        super(cause);
    }
}
