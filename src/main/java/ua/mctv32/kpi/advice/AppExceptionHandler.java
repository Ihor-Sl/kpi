package ua.mctv32.kpi.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ua.mctv32.kpi.dto.ExceptionDto;
import ua.mctv32.kpi.exception.AuthenticationException;
import ua.mctv32.kpi.exception.NotFoundException;

@RestControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ExceptionDto> handle(Exception e) {
        return ResponseEntity.internalServerError().body(new ExceptionDto(e.getMessage()));
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<ExceptionDto> handle(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ExceptionDto(e.getMessage()));
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<ExceptionDto> handle(NotFoundException e) {
        return ResponseEntity.badRequest().body(new ExceptionDto(e.getMessage()));
    }
}
