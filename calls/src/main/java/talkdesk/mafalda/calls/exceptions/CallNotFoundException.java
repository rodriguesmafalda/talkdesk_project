package talkdesk.mafalda.calls.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class CallNotFoundException extends RuntimeException {
    public CallNotFoundException(Long callId) {
        super("Call ID does not exist: " + callId);
    }
}
