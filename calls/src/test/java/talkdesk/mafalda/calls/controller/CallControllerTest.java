package talkdesk.mafalda.calls.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import talkdesk.mafalda.calls.dtos.CallDto;
import talkdesk.mafalda.calls.enums.CallStatus;
import talkdesk.mafalda.calls.enums.CallType;
import talkdesk.mafalda.calls.exceptions.CallNotFoundException;
import talkdesk.mafalda.calls.model.Call;
import talkdesk.mafalda.calls.service.CallService;

import java.sql.Timestamp;
import java.util.Collections;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CallController.class)
class CallControllerTest {

    public static final long CALL_ID = 1;
    public static final String CALLER_NUMBER = "123456";
    public static final String CALLEE_NUMBER = "234986";
    public static final String CALL_TYPE = "INBOUND";
    public static final String ON_CALL = "ON_CALL";
    public static final String ENDED_CALL = "ENDED_CALL";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CallService callService;


    @Test
    void givenValidCall_whenPostingCall_thenCalIsCreated() throws Exception {
        CallDto callDto = new CallDto(CALLER_NUMBER, CALLEE_NUMBER, CALL_TYPE);

        mockMvc.perform(post("/calls/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"callerNumber\":\"" + callDto.getCallerNumber() + "\"," +
                        "\"callerNumber\":\"" + callDto.getCalleeNumber() + "\"," +
                        "\"type\":\"" + callDto.getType() + "\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void givenValidCallId_whenEndTheCall_thenCallIsUpdated() throws Exception {
        mockMvc.perform(patch("/calls/endCall/" + CALL_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(callService, times(1)).endCall(CALL_ID);
    }


    @Test
    void givenValidCallId_whenDeletingCall_thenCallIsDeleted() throws Exception {
        mockMvc.perform(delete("/calls/" + CALL_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(callService, times(1)).deleteCall(CALL_ID);
    }


    @Test
    void givenInvalidCallId_whenDeletingCall_thenResponseIsNotFound() throws Exception {
        doThrow(new CallNotFoundException(CALL_ID)).
                when(callService).deleteCall(CALL_ID);

        mockMvc.perform(delete("/calls/" + CALL_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenType_whenGettingCalls_thenShouldReturnPageInfoByType() throws Exception {
        Call call = createCall();
        Page<Call> page = new PageImpl<>(Collections.singletonList(call));
        given(callService.getCalls(0, 5, CallType.INBOUND, CallStatus.ENDED_CALL)).willReturn(page);

        ResultActions resultActions = mockMvc.perform(get("/calls/")
                .param("page", "0")
                .param("size", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    private Call createCall() {
        Call call = new Call();
        call.setId(CALL_ID);
        call.setCalleeNumber(CALLEE_NUMBER);
        call.setCallerNumber(CALLER_NUMBER);
        call.setType(CALL_TYPE);
        call.setStatus(ENDED_CALL);
        call.setStartTime(new Timestamp(System.currentTimeMillis()));
        call.setEndTime(new Timestamp(System.currentTimeMillis()));
        return call;
    }
}