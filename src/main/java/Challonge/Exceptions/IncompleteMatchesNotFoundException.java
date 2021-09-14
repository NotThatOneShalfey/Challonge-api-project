package Challonge.Exceptions;

public class IncompleteMatchesNotFoundException extends Exception{
    public IncompleteMatchesNotFoundException() {
    }

    public IncompleteMatchesNotFoundException(String message) {
        super(message);
    }

    public IncompleteMatchesNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncompleteMatchesNotFoundException(Throwable cause) {
        super(cause);
    }
}
