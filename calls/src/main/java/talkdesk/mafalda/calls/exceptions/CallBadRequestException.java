package talkdesk.mafalda.calls.exceptions;

public class CallBadRequestException extends RuntimeException {

    private String description;

    public CallBadRequestException(String description) {
        super(description);
    }

    public String getDescription() {
        return this.description;
    }


}


    