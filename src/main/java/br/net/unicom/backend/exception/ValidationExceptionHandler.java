package br.net.unicom.backend.exception;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.net.unicom.backend.model.UsuarioEmailDuplicateException;
import br.net.unicom.backend.model.UsuarioMatriculaDuplicateException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class ValidationExceptionHandler {

     Logger logger = LoggerFactory.getLogger(ValidationExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> notValidInput(MethodArgumentNotValidException e) {
        Map<String, Object> response = new HashMap<>();
        Map<String,String> errorMap = e.getAllErrors()
                .stream()
                .collect(Collectors.toMap(x -> ((FieldError)x).getField(), 
                 b -> b.getDefaultMessage(),(p,q) -> p, LinkedHashMap::new));
        response.put("errors", errorMap);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException e) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errorMap = e.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(x -> ((ConstraintViolation<?>)x).getPropertyPath().toString(), b -> b.getMessage(), (p, q) -> p, LinkedHashMap::new));
        response.put("errors", errorMap);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsuarioEmailDuplicateException.class)
    public ResponseEntity<?> handleUsuarioEmailDuplicateException(UsuarioEmailDuplicateException e) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("email", "email duplicado");
        response.put("errors", errorMap);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsuarioMatriculaDuplicateException.class)
    public ResponseEntity<?> handleUsuarioMatriculaDuplicateException(UsuarioMatriculaDuplicateException e) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("matricula", "matrícula duplicada");
        response.put("errors", errorMap);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}