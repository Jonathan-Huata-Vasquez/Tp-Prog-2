package biblioteca.biblioteca.web.advice;

import biblioteca.biblioteca.application.exception.EntidadNoEncontradaException;
import biblioteca.biblioteca.application.exception.OperacionNoPermitidaException;
import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import biblioteca.biblioteca.domain.exception.ReglaDeNegocioException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.Map;

@ControllerAdvice
public class RestExceptionHandler {

    private ResponseEntity<Object> problem(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(Map.of(
                        "timestamp", Instant.now().toString(),
                        "status", status.value(),
                        "error", status.getReasonPhrase(),
                        "message", message
                ));
    }

    @ExceptionHandler(EntidadNoEncontradaException.class)
    public ResponseEntity<Object> notFound(EntidadNoEncontradaException ex) {
        return problem(HttpStatus.NOT_FOUND, ex.getMessage());
    }
    @ExceptionHandler({ NoHandlerFoundException.class, NoResourceFoundException.class })
    public ResponseEntity<Object> notFound404(Exception ex) {
        // Arma tu payload JSON como vengas usando (aquí devuelvo simple)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Endpoint no encontrado");
    }

    @ExceptionHandler({OperacionNoPermitidaException.class, ReglaDeNegocioException.class})
    public ResponseEntity<Object> conflict(RuntimeException ex) {
        return problem(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler({DatoInvalidoException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<Object> badRequest(Exception ex) {
        return problem(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> methodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("Método HTTP no permitido");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> fallback(Exception ex) {
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "Error inesperado");
    }
}
