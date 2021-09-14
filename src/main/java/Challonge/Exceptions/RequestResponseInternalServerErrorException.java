package Challonge.Exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class RequestResponseInternalServerErrorException extends Exception{
    public RequestResponseInternalServerErrorException() {
    }

    public RequestResponseInternalServerErrorException(String message) {
        super(message);
    }

    public RequestResponseInternalServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestResponseInternalServerErrorException(Throwable cause) {
        super(cause);
    }
}
