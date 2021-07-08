package talkdesk.mafalda.calls.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import talkdesk.mafalda.calls.dtos.CallDto;
import talkdesk.mafalda.calls.enums.CallStatus;
import talkdesk.mafalda.calls.enums.CallType;
import talkdesk.mafalda.calls.exceptions.CallNotFoundException;
import talkdesk.mafalda.calls.model.Call;
import talkdesk.mafalda.calls.model.CallStatistics;
import talkdesk.mafalda.calls.service.CallService;

import java.util.List;

@RestController
@RequestMapping(path = "/calls")
public class CallController {

    public static final Logger LOGGER = LoggerFactory.getLogger(CallController.class);

    private final CallService callService;

    @Autowired
    public CallController(CallService callService) {
        this.callService = callService;
    }

    //TODO: CONTROL THE ERROR OF BAD PARAMS
    @Operation(summary = "Get the pagination of the list of calls")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<Call> getCalls(
            @ParameterObject Pageable pageable,
            @RequestParam(value = "type", required = false, defaultValue = "") CallType type,
            @RequestParam(value = "status", required = false, defaultValue = "") CallStatus status) {
        LOGGER.info("Accessing GET Calls endpoint");
        return callService.getCalls(pageable.getPageNumber(), pageable.getPageSize(), type, status);
    }

    @Operation(summary = "Create one call")
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Call saveCall(@RequestBody @Validated CallDto calldto) {
        LOGGER.info("Accessing POST Calls endpoint");
        return callService.saveCall(calldto);
    }

    @PostMapping("/create/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Call> saveCalls(@RequestBody @Validated List<CallDto> calls) {
        LOGGER.info("Accessing POST Calls endpoint (BULK)");
        return callService.saveCalls(calls);
    }

    @PatchMapping("/end/{callId}")
    @ResponseStatus(HttpStatus.OK)
    public Call endCall(@PathVariable(value = "callId") Long callId) {
        LOGGER.info("Accessing PATCH Calls endpoint for ID: {}", callId);
        return callService.endCall(callId);
    }

    @DeleteMapping("/{callId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCall(@PathVariable(value = "callId") Long callId) throws CallNotFoundException {
        LOGGER.info("Accessing DELETE Call endpoint for ID: {}", callId);
        callService.deleteCall(callId);
    }

    @GetMapping(value = "/statistics")
    public CallStatistics getCallStatistics() {
        LOGGER.info("Accessing GET Call Statistics endpoint");
        return callService.getCallStatistics();
    }

    @ExceptionHandler({CallNotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "The call does not exist")
    public void handleCallNotFoundException(CallNotFoundException exception) {
    }

}
