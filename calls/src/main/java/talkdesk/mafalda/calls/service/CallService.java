package talkdesk.mafalda.calls.service;

import org.springframework.data.domain.Page;
import talkdesk.mafalda.calls.dtos.CallDto;
import talkdesk.mafalda.calls.enums.CallStatus;
import talkdesk.mafalda.calls.enums.CallType;
import talkdesk.mafalda.calls.model.Call;
import talkdesk.mafalda.calls.model.CallStatistics;

import java.util.List;

public interface CallService {

    /**
     * @param call new call
     * @return the call saved
     */
    Call saveCall(CallDto call);

    /**
     * @param calls new calls
     * @return the list of calls saved
     */
    List<Call> saveCalls(List<CallDto> calls);

    /**
     * @param id call id
     * @return the terminated call
     */
    Call endCall(long id);


    /**
     * Delete the call from the database
     *
     * @param id call id
     */
    void deleteCall(long id);

    /**
     * @return the statistics for all calls
     */
    CallStatistics getCallStatistics();

    /**
     * @param pageNumber the page number
     * @param pageSize   the page size
     * @param type       call type
     * @param status     call status
     * @return the page of the list of calls
     */
    Page<Call> getCalls(int pageNumber, int pageSize, String type, String status);

}
