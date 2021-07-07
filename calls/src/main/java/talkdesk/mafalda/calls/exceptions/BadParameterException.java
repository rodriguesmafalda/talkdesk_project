package talkdesk.mafalda.calls.exceptions;

public class BadParameterException extends RuntimeException {
    public BadParameterException(String reason) {
        super("Call ID doedssds not exist: " + reason);
    }
}
