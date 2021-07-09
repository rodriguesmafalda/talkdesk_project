package talkdesk.mafalda.calls.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import talkdesk.mafalda.calls.dtos.CallDto;
import talkdesk.mafalda.calls.exceptions.CallBadRequestException;
import talkdesk.mafalda.calls.exceptions.CallNotFoundException;
import talkdesk.mafalda.calls.model.Call;
import talkdesk.mafalda.calls.model.CallStatistics;
import talkdesk.mafalda.calls.repos.CallRepository;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CallServiceImpl implements CallService {

    public static final Logger LOGGER = LoggerFactory.getLogger(CallServiceImpl.class);
    public static final String ON_CALL = "ON_CALL";
    public static final String ENDED_CALL = "ENDED_CALL";
    public static final String INBOUND = "INBOUND";
    public static final String OUTBOUND = "OUTBOUND";

    private final CallRepository callRepository;


    public CallServiceImpl(CallRepository callRepository) {
        this.callRepository = callRepository;
    }

    @Override
    public Page<Call> getCalls(int pageNumber, int pageSize, String type, String status) {
        Pageable paging = PageRequest.of(pageNumber, pageSize);

        if (!type.isEmpty() && (!type.equals(INBOUND) && !type.equals(OUTBOUND))) {
            throw new CallBadRequestException("The call type must be OUTBOUND or INBOUND not " + type);
        }

        if (!status.isEmpty() && (!status.equals(ON_CALL) && !status.equals(ENDED_CALL))) {
            throw new CallBadRequestException("The call status must be ON_CALL or ENDED_CALL not " + status);
        }

        if (!type.isEmpty() && status.isEmpty()) {
            return this.callRepository.findCallsByType(type, paging);
        } else if (type.isEmpty() && !status.isEmpty()) {
            return this.callRepository.findCallsByStatus(status, paging);
        } else if (type.isEmpty() && status.isEmpty()) {
            return this.callRepository.findAll(paging);
        }

        return this.callRepository.findAllByStatusAndType(status, type, paging);
    }


    @Override
    public Call saveCall(CallDto callDto) {
        checkIfCallIsPossible(callDto);
        Call call = transformToEntity(callDto);
        LOGGER.debug("Creating call: {}", call.getId());
        return callRepository.save(call);
    }

    @Override
    public List<Call> saveCalls(List<CallDto> callsDto) {
        List<Call> calls = callsDto.stream().map(callDto -> {
            checkIfCallIsPossible(callDto);
            Call call = transformToEntity(callDto);
            return call;
        }).collect(Collectors.toList());
        LOGGER.debug("Creating calls: {}", calls);
        return this.callRepository.saveAll(calls);
    }

    @Override
    public Call endCall(long callId) {
        Call call = verifyCallId(callId);
        setCallStatus(call, ENDED_CALL);
        call.setEndTime(new Timestamp(System.currentTimeMillis()));
        LOGGER.debug("Ending the call the call: {}", call.getId());
        return this.callRepository.save(call);
    }

    @Override
    public void deleteCall(long callId) {
        LOGGER.debug("Deleting the call Id: {}", callId);
        Call call = verifyCallId(callId);
        this.callRepository.deleteById(call.getId());
    }

    @Override
    public CallStatistics getCallStatistics() {

        CallStatistics callStatistics = new CallStatistics();

        List<Call> calls = callRepository.findCallsByStatus(ENDED_CALL);

        callStatistics.setTotalInboundCallDuration(getDurationCallByType(INBOUND));
        callStatistics.setTotalOutboundCallDuration(getDurationCallByType(OUTBOUND));

        callStatistics.setTotalNumberOfCalls(calls.size());
        setTotalCallsByCallerNumberAndCalleeNumber(calls, callStatistics);

        callStatistics.setTotalCostByOutbound(getTotalCostByOutbound());
        return callStatistics;
    }

    /**
     * Auxiliary function to check if the call is possible
     * Search if the caller or receiver number is busy
     * Check if the number of the caller and the receiver are the same
     *
     * @param callDto model received
     */
    //TODO: CHANGE EXCEPTION
    private void checkIfCallIsPossible(CallDto callDto) {

        if (callDto.getCalleeNumber().equals(callDto.getCallerNumber())) {
            LOGGER.error("Callee number should be different from than caller number");
            throw new IllegalArgumentException("Callee number should be different from than caller number.");
        }

        List<Call> calls = callRepository.findCallsByStatus(ON_CALL);
        for (Call call : calls) {
            if (call.getCallerNumber().equals(callDto.getCallerNumber())) {
                LOGGER.error("Callee number {} is busy", callDto.getCallerNumber());
                throw new IllegalArgumentException("Caller number " + callDto.getCallerNumber() + " is busy.");
            } else if (call.getCalleeNumber().equals(callDto.getCalleeNumber())) {
                LOGGER.error("Callee number {} is busy", callDto.getCalleeNumber());
                throw new IllegalArgumentException("Callee number " + callDto.getCalleeNumber() + " is busy.");
            }
        }
    }

    /**
     * Auxiliary function to transform the data received in to Entity (Call)
     *
     * @param callDto data received
     * @return the data received on Call entity
     */
    protected Call transformToEntity(CallDto callDto) {
        Call call = new Call();
        call.setCalleeNumber(callDto.getCalleeNumber());
        call.setCallerNumber(callDto.getCallerNumber());
        call.setType(callDto.getType());
        setCallStatus(call, ON_CALL);
        call.setStartTime(new Timestamp(System.currentTimeMillis()));
        return call;
    }

    /**
     * Auxiliary function to verify if the id of the call exists
     *
     * @param callId the id of the call
     * @return the Call
     */
    private Call verifyCallId(long callId) {
        LOGGER.debug("Verifying existence of call for ID: {}", callId);
        return this.callRepository.findById(callId).orElseThrow(() -> {
            LOGGER.error("Call ID does not exist: {}", callId);
            throw new CallNotFoundException(callId);
        });
    }


    /**
     * Auxiliary function to get the total call duration by type
     *
     * @param type call type
     * @return the date and the total call duration
     */
    public Map<String, String> getDurationCallByType(String type) {
        List<Call> calls = this.callRepository.findCallsByTypeAndStatus(type, ENDED_CALL);
        Map<String, Long> statsDuration = new HashMap<>();
        Map<String, String> durationCall = new HashMap<>();

        calls.forEach(call -> {
            long duration = call.getEndTime().getTime() - call.getStartTime().getTime();

            //get date (day-month-year)
            String date = getDate(call.getStartTime());

            //check if the date exists on the map
            if (!statsDuration.containsKey(date)) {
                //add the date and the call duration
                statsDuration.put(date, duration);
            } else {
                //assign to the existing key (date), an increase of the call duration
                statsDuration.put(date, statsDuration.get(date) + duration);
            }
        });

        //Format date to return the correct call duration
        for (Map.Entry<String, Long> entry : statsDuration.entrySet()) {
            durationCall.put(entry.getKey(), timeToString(entry.getValue()));
        }

        return durationCall;
    }


    /**
     * Auxiliary function to set the total of calls by caller Number and callee Number
     *
     * @param calls          list of calls
     * @param callStatistics the model of callStatistics
     */
    private void setTotalCallsByCallerNumberAndCalleeNumber(List<Call> calls, CallStatistics callStatistics) {
        Map<String, Map<String, Long>> callsByCaller = new HashMap<>();
        Map<String, Map<String, Long>> callsByCallee = new HashMap<>();

        for (Call call : calls) {
            String caller = call.getCallerNumber();
            String callee = call.getCalleeNumber();
            parseCallByNumberType(call, callsByCaller, caller);
            parseCallByNumberType(call, callsByCallee, callee);
        }

        callStatistics.setTotalCallsByCallerNumber(callsByCaller);
        callStatistics.setTotalCallsByCalleeNumber(callsByCallee);

    }

    /**
     * Auxiliary function to parse the calls by callee number or caller number
     *
     * @param call              the call
     * @param callsByNumberType map to collect the numberType
     * @param numberType        Callee Number or Caller Number
     */
    public void parseCallByNumberType(Call call, Map<String, Map<String, Long>> callsByNumberType, String numberType) {

        String date = getDate(call.getStartTime());

        //check if the date already exists
        // if not create a new hashmap for this date
        if (!callsByNumberType.containsKey(date)) {
            callsByNumberType.put(date, new HashMap<>());
        }

        //get the date for the map
        Map<String, Long> calls = callsByNumberType.get(date);

        //check if this numberType exists
        if (!calls.containsKey(numberType)) {
            calls.put(numberType, (long) 1);
        } else {
            calls.put(numberType, calls.get(numberType) + 1);
        }
    }


    /**
     * Auxiliary function to get the total call cost by type.
     * Outbound calls cost 0.05 per minute after the first 5 minutes. The first 5 minutes cost 0.10.
     * Inbound calls are free.
     *
     * @return day and total cost
     */
    public Map<String, Double> getTotalCostByOutbound() {

        Map<String, Double> mapTotalCost = new HashMap<>();

        //list of call filtering by type and ended status
        List<Call> calls = callRepository.findCallsByTypeAndStatus(OUTBOUND, ENDED_CALL);

        for (Call call : calls) {
            long durationCallTime = call.getEndTime().getTime() - call.getStartTime().getTime();
            long durationCallTimeMinutes = TimeUnit.MILLISECONDS.toMinutes(durationCallTime) % TimeUnit.HOURS.toMinutes(1);

            //totalCost defined by default with 0.10 because the first 5 durationCallTimeMinutes have a constant value
            double totalCost = 0.10;
            //after the five durationCallTimeMinutes there is an increase of 0.05 per minute
            if (durationCallTimeMinutes > 5)
                totalCost = (durationCallTimeMinutes - 5) * 0.05;

            //check if the date exists on the map
            if (!mapTotalCost.containsKey(getDate(call.getStartTime()))) {
                //add the date and the total call cost
                mapTotalCost.put(getDate(call.getStartTime()), totalCost);
            } else {
                //assign to the existing date, an increase the total call cost
                mapTotalCost.put(getDate(call.getStartTime()), mapTotalCost.get(getDate(call.getStartTime())) + totalCost);
            }
        }
        return mapTotalCost;
    }

    /**
     * Auxiliary function to update the status and the start time of the call, when a call is started
     *
     * @param call   current call
     * @param status the status of the call
     */
    private void setCallStatus(Call call, String status) {
        call.setStatus(status);
    }

    /**
     * Auxiliary function to format the date to a string
     *
     * @param time timestamp
     * @return time into String
     */
    private String getDate(Timestamp time) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(time.getTime());

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        return String.format("%s-%s-%s", day, month, year);
    }

    /**
     * Auxiliary function to return time into String
     *
     * @param time required time
     * @return time into String
     */
    private String timeToString(long time) {
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(time),
                TimeUnit.MILLISECONDS.toMinutes(time) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(time) % TimeUnit.MINUTES.toSeconds(1));
    }

}
