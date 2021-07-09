package talkdesk.mafalda.calls.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import talkdesk.mafalda.calls.dtos.CallDto;
import talkdesk.mafalda.calls.exceptions.CallBadRequestException;
import talkdesk.mafalda.calls.exceptions.CallNotFoundException;
import talkdesk.mafalda.calls.model.Call;
import talkdesk.mafalda.calls.model.CallStatistics;
import talkdesk.mafalda.calls.repos.CallRepository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class CallServiceImplTest {

    public static final long CALL_ID = 1;
    public static final long INVALID_CALL_ID = 2;
    public static final String CALLER_NUMBER = "123456";
    public static final String CALLER_NUMBER2 = "123489756";
    public static final String CALLEE_NUMBER = "234986";
    public static final String CALLEE_NUMBER2 = "234986";
    public static final String CALL_TYPE = "INBOUND";
    public static final String OUTBOUND = "OUTBOUND";
    public static final String ON_CALL = "ON_CALL";
    public static final String ENDED_CALL = "ENDED_CALL";
    public static final String EMPTY_STRING = "";
    public static final int PAGE_NUMBER = 0;
    public static final int PAGE_SIZE = 20;

    @InjectMocks
    private CallServiceImpl callServiceImpl;

    @Mock
    private CallRepository callRepository;

    @Test
    void givenNoParams_whenGetCalls_thenShouldFindCall() {
        Call call = createDummyCall(ENDED_CALL);

        when(this.callRepository.findById(call.getId())).thenReturn(Optional.of(call));
        when(this.callRepository.save(any(Call.class))).thenReturn(call);

        Page<Call> page = new PageImpl<>(Collections.singletonList(call));
        when(this.callRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(this.callRepository.saveAll(anyList())).thenReturn(null);

        Page<Call> callPage = callServiceImpl.getCalls(PAGE_NUMBER, PAGE_SIZE, EMPTY_STRING, EMPTY_STRING);
        assertNotNull(callPage);
        assertEquals(1, callPage.getContent().size());
    }

    @Test
    void givenTypeParams_whenGetCalls_thenShouldFindCall() {
        Call call = createDummyCall(ENDED_CALL);

        when(this.callRepository.findById(call.getId())).thenReturn(Optional.of(call));
        when(this.callRepository.save(any(Call.class))).thenReturn(call);

        Page<Call> page = new PageImpl<>(Collections.singletonList(call));
        when(this.callRepository.findCallsByType(anyString(), any(Pageable.class))).thenReturn(page);
        when(this.callRepository.saveAll(anyList())).thenReturn(null);

        Page<Call> callPage = callServiceImpl.getCalls(PAGE_NUMBER, PAGE_SIZE, OUTBOUND, EMPTY_STRING);
        assertNotNull(callPage);
        assertEquals(1, callPage.getContent().size());
    }

    @Test
    void givenStatusParams_whenGetCalls_thenShouldFindCall() {
        Call call = createDummyCall(ENDED_CALL);

        when(this.callRepository.findById(call.getId())).thenReturn(Optional.of(call));
        when(this.callRepository.save(any(Call.class))).thenReturn(call);

        Page<Call> page = new PageImpl<>(Collections.singletonList(call));
        when(this.callRepository.findCallsByStatus(anyString(), any(Pageable.class))).thenReturn(page);
        when(this.callRepository.saveAll(anyList())).thenReturn(null);

        Page<Call> callPage = callServiceImpl.getCalls(PAGE_NUMBER, PAGE_SIZE, EMPTY_STRING, ENDED_CALL);
        assertNotNull(callPage);
        assertEquals(1, callPage.getContent().size());
    }

    @Test
    void givenStatusAndTypeParams_whenGetCalls_thenShouldFindCall() {
        Call call = createDummyCall(ENDED_CALL);

        when(this.callRepository.findById(call.getId())).thenReturn(Optional.of(call));
        when(this.callRepository.save(any(Call.class))).thenReturn(call);

        Page<Call> page = new PageImpl<>(Collections.singletonList(call));
        when(this.callRepository.findAllByStatusAndType(anyString(), anyString(), any(Pageable.class))).thenReturn(page);
        when(this.callRepository.saveAll(anyList())).thenReturn(null);

        Page<Call> callPage = callServiceImpl.getCalls(PAGE_NUMBER, PAGE_SIZE, OUTBOUND, ENDED_CALL);
        assertNotNull(callPage);
        assertEquals(1, callPage.getContent().size());
    }

    @Test
    void givenInvalidStatusParams_whenGetCalls_thenShouldFindCall() {
        Call call = createDummyCall(ENDED_CALL);

        when(this.callRepository.findById(call.getId())).thenReturn(Optional.of(call));
        when(this.callRepository.save(any(Call.class))).thenReturn(call);

        Page<Call> page = new PageImpl<>(Collections.singletonList(call));
        when(this.callRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(this.callRepository.saveAll(anyList())).thenReturn(null);

        Exception exception = assertThrows(CallBadRequestException.class, () ->
                this.callServiceImpl.getCalls(PAGE_NUMBER, PAGE_SIZE, EMPTY_STRING, OUTBOUND));

        String expectedMessage = "The call status must be ON_CALL or ENDED_CALL not " + OUTBOUND;
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }


    @Test
    void givenInvalidTypeParams_whenGetCalls_thenShouldFindCall() {
        Call call = createDummyCall(ENDED_CALL);

        when(this.callRepository.findById(call.getId())).thenReturn(Optional.of(call));
        when(this.callRepository.save(any(Call.class))).thenReturn(call);

        Page<Call> page = new PageImpl<>(Collections.singletonList(call));
        when(this.callRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(this.callRepository.saveAll(anyList())).thenReturn(null);

        Exception exception = assertThrows(CallBadRequestException.class, () ->
                this.callServiceImpl.getCalls(PAGE_NUMBER, PAGE_SIZE, ENDED_CALL, EMPTY_STRING));

        String expectedMessage = "The call type must be OUTBOUND or INBOUND not " + ENDED_CALL;
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }


    @Test
    void givenValidCall_whenAddingCall_thenShouldCreateCall() {
        CallDto callDto = new CallDto(CALLER_NUMBER, CALLEE_NUMBER, CALL_TYPE);

        when(callRepository.save(any(Call.class))).thenReturn(this.callServiceImpl.transformToEntity(callDto));

        Call callResult = callServiceImpl.saveCall(callDto);
        verify(callRepository, times(1)).save(any(Call.class));
        assertNotNull(callResult);
        assertNotNull(callResult.getCalleeNumber());
    }

    @Test
    void givenValidListOfCall_whenAddingCalls_thenShouldCreateCalls() {
        CallDto callDto = new CallDto(CALLER_NUMBER, CALLEE_NUMBER, CALL_TYPE);

        CallDto callDto2 = new CallDto(CALLER_NUMBER2, CALLEE_NUMBER2, CALL_TYPE);
        List<CallDto> calls = new ArrayList<>();
        calls.add(callDto);
        calls.add(callDto2);

        List<Call> callsList = new ArrayList<>();
        for (CallDto call : calls) {
            Call transformToEntity = this.callServiceImpl.transformToEntity(call);
            callsList.add(transformToEntity);
        }

        when(callRepository.saveAll(anyList())).thenReturn(callsList);

        List<Call> callResult = callServiceImpl.saveCalls(calls);
        verify(callRepository, times(1)).saveAll(anyList());
        assertNotNull(callResult);
        assertEquals(callResult.get(0).getCalleeNumber(), CALLEE_NUMBER);

    }


    @Test
    void givenEqualCallerNumberAndCalleeNumber_whenAddingCall_thenThrowIllegalArgumentException() {
        CallDto callDto = new CallDto(CALLEE_NUMBER, CALLEE_NUMBER, CALL_TYPE);

        when(callRepository.save(any(Call.class))).thenReturn(this.callServiceImpl.transformToEntity(callDto));

        Exception exception = assertThrows(CallBadRequestException.class, () ->
                callServiceImpl.saveCall(callDto));

        String expectedMessage = "Callee number should be different from than caller number";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }


    @Test
    void givenCallerNumberBusy_whenAddingCall_thenThrowIllegalArgumentException() {

        Call call = createDummyCall(ON_CALL);
        CallDto callDto = new CallDto(CALLER_NUMBER, CALLEE_NUMBER, CALL_TYPE);

        when(this.callRepository.findById(call.getId())).thenReturn(Optional.of(call));
        when(this.callRepository.findById(call.getId())).thenReturn(Optional.of(call));
        when(this.callRepository.findCallsByStatus(ON_CALL)).thenReturn(Collections.singletonList(call));
        when(this.callRepository.save(any(Call.class))).thenReturn(call);

        Exception exception = assertThrows(CallBadRequestException.class, () ->
                callServiceImpl.saveCall(callDto));

        String expectedMessage = "Caller number " + CALLER_NUMBER + " is busy";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void givenCalleeNumberBusy_whenAddingCall_thenThrowIllegalArgumentException() {

        Call call = createDummyCall(ON_CALL);
        CallDto callDto = new CallDto(CALLER_NUMBER2, CALLEE_NUMBER, CALL_TYPE);

        when(this.callRepository.findById(call.getId())).thenReturn(Optional.of(call));
        when(this.callRepository.findById(call.getId())).thenReturn(Optional.of(call));
        when(this.callRepository.findCallsByStatus(ON_CALL)).thenReturn(Collections.singletonList(call));
        when(this.callRepository.save(any(Call.class))).thenReturn(call);


        Exception exception = assertThrows(CallBadRequestException.class, () ->
                callServiceImpl.saveCall(callDto));

        String expectedMessage = "Callee number " + CALLEE_NUMBER + " is busy";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }


    @Test
    void givenValidCallId_whenDeletingCall_thenShouldFindAndDeleteCall() {
        Call call = createDummyCall(ENDED_CALL);
        when(callRepository.findById(call.getId())).thenReturn(Optional.of(call));
        callServiceImpl.deleteCall(call.getId());
        verify(callRepository).deleteById(call.getId());
    }

    @Test
    void givenInvalidCallId_whenDeletingCall_thenThrowReturnCallNotFoundException() {
        Call call = createDummyCall(ENDED_CALL);
        when(callRepository.findById(call.getId())).thenReturn(Optional.of(call));

        Exception exception = assertThrows(CallNotFoundException.class, () ->
                callServiceImpl.deleteCall(INVALID_CALL_ID));

        String expectedMessage = "Call ID does not exist: " + INVALID_CALL_ID;
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }


    @Test
    void givenValidCallId_whenEndingCall_thenShouldFindAndEndedCall() {
        Call call = createDummyCall(ON_CALL);

        when(this.callRepository.findById(call.getId())).thenReturn(Optional.of(call));
        when(this.callRepository.findById(call.getId())).thenReturn(Optional.of(call));
        when(this.callRepository.save(any(Call.class))).thenReturn(call);

        Call final_call = callServiceImpl.endCall(call.getId());
        verify(callRepository).save(call);
        assertEquals(final_call.getStatus(), ENDED_CALL);

    }


    @Test
    void givenValidRequest_whenGetCallStatistics_thenShouldReturnStatisticsForAllCalls() {
        Call call = createDummyCall(ENDED_CALL);

        when(this.callRepository.findById(call.getId())).thenReturn(Optional.of(call));

        when(this.callRepository.findCallsByStatus(ENDED_CALL)).thenReturn(Collections.singletonList(call));
        when(this.callRepository.findCallsByTypeAndStatus(CALL_TYPE, ENDED_CALL)).thenReturn(Collections.singletonList(call));
        when(this.callRepository.findCallsByTypeAndStatus(OUTBOUND, ENDED_CALL)).thenReturn(Collections.singletonList(call));
        when(this.callRepository.saveAll(anyList())).thenReturn(null);

        CallStatistics callStatistics = callServiceImpl.getCallStatistics();

        assertNotNull(callStatistics);
        assertEquals(1, callStatistics.getTotalNumberOfCalls());
    }

    private Call createDummyCall(String status) {
        Call call = new Call();
        call.setId(CALL_ID);
        call.setCalleeNumber(CALLEE_NUMBER);
        call.setCallerNumber(CALLER_NUMBER);
        call.setStartTime(Timestamp.valueOf("2021-07-09 10:30:10.11"));
        call.setEndTime(Timestamp.valueOf("2021-07-09 11:30:10.11"));
        call.setType(OUTBOUND);
        call.setStatus(status);
        return call;
    }


}
