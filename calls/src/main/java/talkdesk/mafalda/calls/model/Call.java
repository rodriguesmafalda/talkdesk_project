package talkdesk.mafalda.calls.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * Save all calls and their associated information in the database
 */
@Entity
@Table(name = "call")
public class Call {

    public Call() {
    }

    /**
     * Id of the call (primary key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    /**
     * the phone number of the caller
     */
    @NotNull
    @Column(name = "caller_number")
    private String callerNumber;

    /**
     * the phone number of the callee
     */
    @NotNull
    @Column(name = "callee_number")
    private String calleeNumber;

    /**
     * start time of the call
     */
    @Column(name = "start_time")
    private Timestamp startTime;

    /**
     * end time of the call
     */
    @Column(name = "end_time")
    private Timestamp endTime;

    /**
     * type of the call (Inbound or Outbound)
     */
    @NotNull
    @Column(name = "type")
    private String type;

    /**
     * status of the call (ON_CALL or ENDED_CALL)
     */
    @Column(name = "status")
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
