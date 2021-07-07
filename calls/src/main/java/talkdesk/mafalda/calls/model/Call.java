package talkdesk.mafalda.calls.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * Save all calls and their associated information in the database
 */
@Entity
@Table(name = "Call")
public class Call {

    public Call() {
    }

    /**
     * Id of the call (primary key)
     */
    @Id
    @GeneratedValue
    private long id;

    /**
     * the phone number of the caller
     */
    @NotEmpty
    private String callerNumber;

    /**
     * the phone number of the callee
     */
    @NotEmpty
    private String calleeNumber;

    /**
     * start timestamp of the call
     */
    private Timestamp startTime;

    /**
     * end timestamp of the call
     */
    private Timestamp endTime;

    /**
     * type of the call (Inbound or Outbound)
     */
    @NotEmpty
    private String type;

    /**
     * status of the call (ON_CALL or ENDED_CALL)
     */
    private String status;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
