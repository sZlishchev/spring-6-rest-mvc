package guru.springframework.spring6restmvc.controllers;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ErrorController {
    @ExceptionHandler(TransactionSystemException.class)
    ResponseEntity handleJPAValidation(TransactionSystemException exception) {
        final var responseEntity = ResponseEntity.badRequest();

        if (exception.getCause().getCause() instanceof ConstraintViolationException) {
            final var cve = (ConstraintViolationException) exception.getCause().getCause();

            final var errors = cve.getConstraintViolations().stream()
                    .map(constraintViolation -> {
                        final var errorMap = new HashMap<>();

                        errorMap.put(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());

                        return errorMap;
                    }).collect(Collectors.toList());
            
            return responseEntity.body(errors);
        }

        return responseEntity.build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity handleBinding(MethodArgumentNotValidException exception) {

        final var errorsList = exception.getFieldErrors().stream()
                .map(error -> {
                    final var errorMap = new HashMap<>();
                    errorMap.put(error.getField(), error.getDefaultMessage());
                    return errorMap;
                }).collect(Collectors.toList());

        return ResponseEntity.badRequest().body(errorsList);
    }
}
