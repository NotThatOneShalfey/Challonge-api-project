package Challonge.Exceptions;

public class MatchesNotFoundException extends Exception{

    public MatchesNotFoundException() {
    }

    public MatchesNotFoundException(String message) {
        super(message);
    }

    public MatchesNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MatchesNotFoundException(Throwable cause) {
        super(cause);
    }
}
