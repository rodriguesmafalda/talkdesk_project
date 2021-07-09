package talkdesk.mafalda.calls.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class CallBadRequestException extends RuntimeException {
    public CallBadRequestException(String message) {
        super(message);
    }
}
