package talkdesk.mafalda.calls.dtos;

import javax.validation.constraints.Size;
import java.io.Serializable;


public class CallDto implements Serializable {

    private static final long serialVersionUID = 7400544121058353704L;

    public CallDto() {
    }

    @Size(min = 0, max = 20)
    private String callerNumber;

    @Size(min = 0, max = 20)
    private String calleeNumber;

    @Size(min = 0, max = 20)
    private String type;


    public String getCallerNumber() {
        return callerNumber;
    }

    public void setCallerNumber(String callerNumber) {
        this.callerNumber = callerNumber;
    }

    public String getCalleeNumber() {
        return calleeNumber;
    }

    public void setCalleeNumber(String calleeNumber) {
        this.calleeNumber = calleeNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public CallDto(String callerNumber, String calleeNumber, String type) {
        this.callerNumber = callerNumber;
        this.calleeNumber = calleeNumber;
        this.type = type;
    }
}

