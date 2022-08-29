package com.rkey.returnmgmt.view.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class PendingReturnNotFoundAdvice {
    @ResponseBody
    @ExceptionHandler(PendingReturnNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String pendingReturnNotFoundHandler(PendingReturnNotFoundException ex) {
        return ex.getMessage();
    }
}
