package com.allan.task.manager.shared;

import com.allan.task.manager.client.dto.DuplicateClientError;
import com.allan.task.manager.client.exceptions.DuplicateClientException;
import com.allan.task.manager.shared.dto.CustomError;
import com.allan.task.manager.shared.exceptions.AlreadyExistsException;
import com.allan.task.manager.shared.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.NOT_FOUND;
        CustomError err = new CustomError(
                Instant.now(),
                status.value(),
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<CustomError> alreadyExists(AlreadyExistsException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.CONFLICT;
        CustomError err = new CustomError(
                Instant.now(),
                status.value(),
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(DuplicateClientException.class)
    public ResponseEntity<DuplicateClientError> duplicateClient(
            DuplicateClientException e,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.CONFLICT;

        DuplicateClientError err = new DuplicateClientError(
                Instant.now(),
                status.value(),
                e.getMessage(),
                request.getRequestURI(),
                e.getDuplicates()
        );

        return ResponseEntity.status(status).body(err);
    }

    // Temporary fallback.
    // I'll replace this with specific handlers as I add custom exceptions.
    // TODO: Replace this with specific exception handlers.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomError> exception(
            Exception e,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        CustomError err = new CustomError(
                Instant.now(),
                status.value(),
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(err);
    }
}
