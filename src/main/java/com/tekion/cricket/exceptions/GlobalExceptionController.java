package com.tekion.cricket.exceptions;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionController {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> messageNotReadableException(HttpMessageNotReadableException hnmre){
        return ResponseEntity.badRequest().body(hnmre.getMessage());
    }

    @ExceptionHandler(JsonMappingException.class)
    public ResponseEntity<String> jsonMappingException(JsonMappingException jme){
        return ResponseEntity.badRequest().body(jme.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class )
    public ResponseEntity<String> illegalStateHandler(IllegalStateException ise){
        return ResponseEntity.badRequest().body(ise.getMessage());
    }
}
