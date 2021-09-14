package Challonge.Exceptions;

public class CompleteMatchesNotFoundException extends Exception{

    public CompleteMatchesNotFoundException() {
    }

    public CompleteMatchesNotFoundException(String message) {
        super(message);
    }

    public CompleteMatchesNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CompleteMatchesNotFoundException(Throwable cause) {
        super(cause);
    }
}
