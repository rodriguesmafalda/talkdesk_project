package talkdesk.mafalda.calls.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import talkdesk.mafalda.calls.dtos.CallDto;
import talkdesk.mafalda.calls.model.Call;
import talkdesk.mafalda.calls.repos.CallRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class CallServiceImplTest {

    public static final long CALL_ID = 1;
    public static final String CALLER_NUMBER = "123456";
    public static final String CALLEE_NUMBER = "234986";
    public static final String CALL_TYPE = "INBOUND";

    @InjectMocks
    private CallServiceImpl callServiceImpl;

    @Mock
    private CallRepository callRepository;


    @Test
    void givenCall_whenAddingCall_thenShouldFindCall() {
        CallDto callDto = new CallDto(CALLER_NUMBER, CALLEE_NUMBER, CALL_TYPE);

        Call call = new Call();
        call.setCalleeNumber(CALLEE_NUMBER);
        call.setCallerNumber(CALLER_NUMBER);
        call.setType(CALL_TYPE);

        when(callRepository.save(any(Call.class))).thenReturn(call);

        Call callResult = callServiceImpl.saveCall(callDto);

        assertNotNull(callResult);
    }

    @Test
    void givenValidCallId_whenDeletingCall_thenShouldFindAndDeleteCall() {
        Call call = new Call();
        call.setCalleeNumber(CALLEE_NUMBER);
        call.setCallerNumber(CALLER_NUMBER);
        call.setType(CALL_TYPE);
        call.setId(CALL_ID);
        when(callRepository.findById(call.getId())).thenReturn(Optional.of(call));
        callServiceImpl.deleteCall(CALL_ID);
        verify(callRepository).deleteById(CALL_ID);
    }

}