package uk.ac.brunel.exceptions;

public class IncompleteObjectDataException extends RuntimeException {
    public IncompleteObjectDataException(String message) {
        super(message);
    }

    public IncompleteObjectDataException(String message, Throwable err) {
        super(message, err);
    }
}
