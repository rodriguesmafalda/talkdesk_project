package talkdesk.mafalda.calls.exceptions;

public class CallNotFoundException extends RuntimeException {
    public CallNotFoundException(Long callId) {
        super("Call ID does not exist: " + callId);
    }
}
