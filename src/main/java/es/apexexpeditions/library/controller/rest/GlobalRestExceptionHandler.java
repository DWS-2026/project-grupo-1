package es.apexexpeditions.library.controller.rest;




// region =========== imports =================
import es.apexexpeditions.library.dto.resterror.RestErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;
// endregion




@RestControllerAdvice
public class GlobalRestExceptionHandler {
    // region getUtcTimestamp
    // helper that calculates UTC timestamp string
    private String getUtcTimestamp() {
        return java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC)
                .format(java.time.format.DateTimeFormatter.ISO_INSTANT);
    }
    // endregion


    // region handleNotFound
    // manages 404 cases
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<RestErrorDTO> handleNotFound(NoSuchElementException ex) {
        RestErrorDTO error = new RestErrorDTO(
                getUtcTimestamp(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                "The requested resource was not found."
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    // endregion


    // region handleBadRequest
    // manages 400 cases
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RestErrorDTO> handleBadRequest(IllegalArgumentException ex) {
        RestErrorDTO error = new RestErrorDTO(
                getUtcTimestamp(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    // endregion


    // region handleGlobal
    // manages 500 cases
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestErrorDTO> handleGlobal(Exception ex) {
        ex.printStackTrace();

        RestErrorDTO error = new RestErrorDTO(
                getUtcTimestamp(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An internal server error occurred. Please contact the administrator."
        );

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    // endregion


    // region handleAccessDenied
    @ExceptionHandler (AccessDeniedException.class)
    public ResponseEntity<RestErrorDTO> handleAccessDenied (AccessDeniedException ex) {
        RestErrorDTO error = new RestErrorDTO(
                getUtcTimestamp(),
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                "You do not have sufficient permissions to perform this action."
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }
    // endregion


    // region handleValidation
    // catches @Valid failures in dtos and returns 400
    @ExceptionHandler (MethodArgumentNotValidException.class)
    public ResponseEntity<RestErrorDTO> handleValidation(MethodArgumentNotValidException ex) {
        // Collect all validation error messages into a single string
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        RestErrorDTO error = new RestErrorDTO(
                getUtcTimestamp(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                details
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    // endregion
}