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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
            LOGGER.error("The call type must be OUTBOUND or INBOUND not {}", type);
            throw new CallBadRequestException("The call type must be OUTBOUND or INBOUND not " + type);
        }

        if (!status.isEmpty() && (!status.equals(ON_CALL) && !status.equals(ENDED_CALL))) {
            LOGGER.error("The call status must be ON_CALL or ENDED_CALL not {}", status);
            throw new CallBadRequestException("The call status must be ON_CALL or ENDED_CALL not " + status);
        }
        LOGGER.debug("Get the calls");
        if (!type.isEmpty() && status.isEmpty()) {
            return this.callRepository.findCallsByType(type, paging);
        } else if (type.isEmpty() && !status.isEmpty()) {
            return this.callRepository.findCallsByStatus(status, paging);
        } else if (type.isEmpty()) {
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
            return transformToEntity(callDto);
        }).collect(Collectors.toList());
        LOGGER.debug("Creating calls: {}", calls);
        return this.callRepository.saveAll(calls);
    }

    @Override
    public Call endCall(long callId) {
        Call call = checkIfCallEnded(callId);
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
    private void checkIfCallIsPossible(CallDto callDto) {

        if (callDto.getCalleeNumber().equals(callDto.getCallerNumber())) {
            LOGGER.error("Callee number should be different from than caller number");
            throw new CallBadRequestException("Callee number should be different from than caller number.");
        }

        List<Call> calls = callRepository.findCallsByStatus(ON_CALL);
        for (Call call : calls) {
            if (call.getCallerNumber().equals(callDto.getCallerNumber())) {
                LOGGER.error("Callee number {} is busy", callDto.getCallerNumber());
                throw new CallBadRequestException("Caller number " + callDto.getCallerNumber() + " is busy.");
            } else if (call.getCalleeNumber().equals(callDto.getCalleeNumber())) {
                LOGGER.error("Callee number {} is busy", callDto.getCalleeNumber());
                throw new CallBadRequestException("Callee number " + callDto.getCalleeNumber() + " is busy.");
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
     * Auxiliary function to verify if the id of the call exists and if the call is already ended
     *
     * @param callId the id of the call
     * @return the Call
     */
    private Call checkIfCallEnded(long callId) {
        LOGGER.debug("Verifying existence of call for ID: {}", callId);
        Optional<Call> callOptional = this.callRepository.findById(callId);
        if (callOptional.isPresent()) {
            Call call = callOptional.get();
            if (call.getStatus().equals(ENDED_CALL)) {
                LOGGER.error("The call ID is already ended {}", callId);
                throw new CallBadRequestException("The call is already ended: " + callId);

            }
            return call;
        }
        LOGGER.error("Call ID does not exist: {}", callId);
        throw new CallNotFoundException(callId);
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
        Map<String, Long> durationByTypeAggregatedByDay = new HashMap<>();
        Map<String, String> totalDurationCall = new HashMap<>();

        calls.forEach(call -> {
            long callDuration = call.getEndTime().getTime() - call.getStartTime().getTime();
            String date = formatDate(call.getStartTime());
            if (!durationByTypeAggregatedByDay.containsKey(date)) {
                durationByTypeAggregatedByDay.put(date, callDuration);
            } else {
                durationByTypeAggregatedByDay.put(date, durationByTypeAggregatedByDay.get(date) + callDuration);
            }
        });

        for (Map.Entry<String, Long> entry : durationByTypeAggregatedByDay.entrySet()) {
            totalDurationCall.put(entry.getKey(), formatCallDurationTime(entry.getValue()));
        }

        return totalDurationCall;
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
            totalCallsByNumberType(call, callsByCaller, caller);
            totalCallsByNumberType(call, callsByCallee, callee);
        }

        callStatistics.setTotalCallsByCallerNumber(callsByCaller);
        callStatistics.setTotalCallsByCalleeNumber(callsByCallee);

    }

    /**
     * Auxiliary function to parse the calls by callee number or caller number
     *
     * @param call              the call
     * @param callsByNumberType map to collect the numberType aggregated by date
     * @param numberType        Callee Number or Caller Number
     */
    public void totalCallsByNumberType(Call call, Map<String, Map<String, Long>> callsByNumberType, String numberType) {

        String date = formatDate(call.getStartTime());

        if (!callsByNumberType.containsKey(date)) {
            callsByNumberType.put(date, new HashMap<>());
        }

        Map<String, Long> calls = callsByNumberType.get(date);

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

        Map<String, Double> totalCostByOutbound = new HashMap<>();

        List<Call> calls = callRepository.findCallsByTypeAndStatus(OUTBOUND, ENDED_CALL);

        for (Call call : calls) {
            long durationCallTime = call.getEndTime().getTime() - call.getStartTime().getTime();
            long durationCallTimeMinutes = TimeUnit.MILLISECONDS.toMinutes(durationCallTime) % TimeUnit.HOURS.toMinutes(1);

            double totalCost = 0.10;
            if (durationCallTimeMinutes > 5)
                totalCost = (durationCallTimeMinutes - 5) * 0.05;

            if (!totalCostByOutbound.containsKey(formatDate(call.getStartTime()))) {
                totalCostByOutbound.put(formatDate(call.getStartTime()), totalCost);
            } else {
                totalCostByOutbound.put(formatDate(call.getStartTime()), totalCostByOutbound.get(formatDate(call.getStartTime())) + totalCost);
            }
        }
        return totalCostByOutbound;
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
     * Auxiliary function to format time value to a date (dd-mm-yyyy)
     *
     * @param time time
     * @return time into String
     */
    private String formatDate(Timestamp time) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(time);
    }

    /**
     * Auxiliary function to format the call duration into hh:mm:ss
     *
     * @param callDuration call duration
     * @return call duration into String
     */
    private String formatCallDurationTime(long callDuration) {
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(callDuration),
                TimeUnit.MILLISECONDS.toMinutes(callDuration) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(callDuration) % TimeUnit.MINUTES.toSeconds(1));
    }

}
