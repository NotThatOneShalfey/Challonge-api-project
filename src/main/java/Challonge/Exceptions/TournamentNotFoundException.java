package Challonge.Exceptions;

public class TournamentNotFoundException extends Exception{

    public TournamentNotFoundException() {
    }

    public TournamentNotFoundException(String message) {
        super(message);
    }

    public TournamentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public TournamentNotFoundException(Throwable cause) {
        super(cause);
    }
}
