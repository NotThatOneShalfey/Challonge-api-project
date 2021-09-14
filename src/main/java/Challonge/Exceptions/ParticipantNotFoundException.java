package Challonge.Exceptions;

public class ParticipantNotFoundException extends Exception{

    public ParticipantNotFoundException() {
    }

    public ParticipantNotFoundException(String message) {
        super(message);
    }

    public ParticipantNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParticipantNotFoundException(Throwable cause) {
        super(cause);
    }
}
