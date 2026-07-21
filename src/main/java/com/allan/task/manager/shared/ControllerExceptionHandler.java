package com.allan.task.manager.shared;

import com.allan.task.manager.client.dto.DuplicateClientError;
import com.allan.task.manager.client.exceptions.DuplicateClientException;
import com.allan.task.manager.comment.exception.CommentAccessDeniedException;
import com.allan.task.manager.shared.dto.CustomError;
import com.allan.task.manager.shared.dto.ValidationError;
import com.allan.task.manager.shared.exceptions.AlreadyExistsException;
import com.allan.task.manager.shared.exceptions.BadRequestException;
import com.allan.task.manager.shared.exceptions.ForbiddenException;
import com.allan.task.manager.shared.exceptions.ResourceNotFoundException;
import com.allan.task.manager.workspace.exception.WorkspaceAccessDeniedException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.UUID;

@ControllerAdvice
public class ControllerExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ControllerExceptionHandler.class);

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> validation(
            MethodArgumentNotValidException e,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ValidationError err = new ValidationError(
                Instant.now(),
                status.value(),
                "Validation error",
                request.getRequestURI()
        );

        e.getBindingResult()
                .getFieldErrors()
                .forEach(fieldError -> err.addError(
                        fieldError.getField(),
                        fieldError.getDefaultMessage()
                ));

        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<CustomError> forbidden(
            ForbiddenException e,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.FORBIDDEN;

        CustomError err = new CustomError(
                Instant.now(),
                status.value(),
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<CustomError> badRequest(
            BadRequestException e,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        CustomError err = new CustomError(
                Instant.now(),
                status.value(),
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<CustomError> typeMismatch(
            MethodArgumentTypeMismatchException e,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        String message = "Invalid parameter";

        if (e.getRequiredType() != null && e.getRequiredType().equals(UUID.class)) {
            message = "Invalid UUID parameter: " + e.getName();
        }

        CustomError err = new CustomError(
                Instant.now(),
                status.value(),
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CustomError> messageNotReadable(
            HttpMessageNotReadableException e,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        CustomError err = new CustomError(
                Instant.now(),
                status.value(),
                "Invalid request body",
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<CustomError> noResourceFound(
            NoResourceFoundException e,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        CustomError err = new CustomError(
                Instant.now(),
                status.value(),
                "Resource not found",
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomError> exception(
            Exception e,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        log.error("Unexpected server error at {}", request.getRequestURI(), e);

        CustomError err = new CustomError(
                Instant.now(),
                status.value(),
                "Unexpected server error",
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(err);
    }
}