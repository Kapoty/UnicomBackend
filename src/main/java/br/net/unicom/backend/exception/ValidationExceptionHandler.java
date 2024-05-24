package br.net.unicom.backend.exception;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.net.unicom.backend.model.exception.EquipeInvalidaException;
import br.net.unicom.backend.model.exception.JornadaStatusNaoEncontradoException;
import br.net.unicom.backend.model.exception.JornadaStatusNaoPermitidoException;
import br.net.unicom.backend.model.exception.PapelInvalidoException;
import br.net.unicom.backend.model.exception.PontoConfiguracaoNaoEncontradoException;
import br.net.unicom.backend.model.exception.RegistroPontoFullException;
import br.net.unicom.backend.model.exception.RegistroPontoLockedException;
import br.net.unicom.backend.model.exception.RegistroPontoUnauthorizedException;
import br.net.unicom.backend.model.exception.UsuarioEmailDuplicateException;
import br.net.unicom.backend.model.exception.UsuarioMatriculaDuplicateException;
import br.net.unicom.backend.model.exception.UsuarioNaoRegistraPontoHojeException;
import br.net.unicom.backend.model.exception.UsuarioSemContratoException;
import br.net.unicom.backend.model.exception.UsuarioSemJornadaException;
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

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNoSuchElementException(NoSuchElementException e) {
        logger.error(e.getMessage(), e);
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("elemento", "elemento não encontrado");
        response.put("errors", errorMap);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsuarioSemContratoException.class)
    public ResponseEntity<?> handleUsuarioSemContratoException(UsuarioSemContratoException e) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("contrato", "usuário sem contrato");
        response.put("errors", errorMap);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsuarioSemJornadaException.class)
    public ResponseEntity<?> handleUsuarioSemJornadaException(UsuarioSemJornadaException e) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("jornada", "usuário sem jornada");
        response.put("errors", errorMap);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsuarioNaoRegistraPontoHojeException.class)
    public ResponseEntity<?> handleUsuarioNaoRegistraPontoHojeException(UsuarioNaoRegistraPontoHojeException e) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("hoje", "usuário não registra ponto hoje");
        response.put("errors", errorMap);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RegistroPontoFullException.class)
    public ResponseEntity<?> handleRegistroPontoFullException(RegistroPontoFullException e) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("full", "registro já preenchido");
        response.put("errors", errorMap);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RegistroPontoLockedException.class)
    public ResponseEntity<?> handleRegistroPontoLockedException(RegistroPontoLockedException e) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("locked", "usuário deve aguardar antes de registrar ponto novamente");
        response.put("errors", errorMap);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RegistroPontoUnauthorizedException.class)
    public ResponseEntity<?> handleRegistroPontoUnauthorizedException(RegistroPontoUnauthorizedException e) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("token", "dispositivo não autorizado");
        response.put("errors", errorMap);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PontoConfiguracaoNaoEncontradoException.class)
    public ResponseEntity<?> handlePontoConfiguracaoNaoEncontradoException(PontoConfiguracaoNaoEncontradoException e) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("pontoConfiguracao", "ponto configuração não encontrado");
        response.put("errors", errorMap);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JornadaStatusNaoEncontradoException.class)
    public ResponseEntity<?> handleJornadaStatusNaoEncontradoException(JornadaStatusNaoEncontradoException e) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("jornadaStatus", "status não encontrado");
        response.put("errors", errorMap);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JornadaStatusNaoPermitidoException.class)
    public ResponseEntity<?> handleJornadaStatusNaoPermitidoException(JornadaStatusNaoPermitidoException e) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("jornadaStatus", "status não permitido:" + e.getMessage());
        response.put("errors", errorMap);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PapelInvalidoException.class)
    public ResponseEntity<?> handlePapelInvalidoException(PapelInvalidoException e) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("papel", "papel inválido");
        response.put("errors", errorMap);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EquipeInvalidaException.class)
    public ResponseEntity<?> handleEquipeInvalidaException(EquipeInvalidaException e) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("equipe", "equipe inválida");
        response.put("errors", errorMap);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /*@ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {

        logger.error(e.getMessage());

        Map<String, Object> response = new HashMap<>();
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("excecao", e.getMessage());
        response.put("errors", errorMap);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }*/
}