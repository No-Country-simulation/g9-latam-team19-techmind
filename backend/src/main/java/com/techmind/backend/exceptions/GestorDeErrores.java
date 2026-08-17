package com.techmind.backend.exceptions;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.List;


@RestControllerAdvice
public class GestorDeErrores {

    private static final Logger logger = LoggerFactory.getLogger(GestorDeErrores.class);

    // Esto es lo que se ejecuta cuando fallan las anotaciones de Bean Validation:* @NotBlank, @NotNull, @Size, etc.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<DatosFaltantesError>> gestionarDatosFaltantes(MethodArgumentNotValidException exception) {
        var errores = exception.getFieldErrors()
                .stream()
                .map(DatosFaltantesError::new)
                .toList();

        return ResponseEntity.badRequest().body(errores);
    }

    // Validaciones manuales propias del proyecto, lo que arrojabamos en el proyecto de vod mell api
    @ExceptionHandler(ValidacionException.class)
    public ResponseEntity<ErrorResponse> gestionarErrorDeValidacion(ValidacionException exception) {
        var respuesta = new ErrorResponse(
                "VALIDACION_INCORRECTA",
                exception.getMessage()
        );

        return ResponseEntity.badRequest().body(respuesta);
    }

    //JSON mal escrito o incompleto
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> gestionarJsonInvalido(HttpMessageNotReadableException exception) {
        var respuesta = new ErrorResponse(
                "JSON_INVALIDO",
                "El cuerpo de la solicitud contiene un JSON inválido"
        );

        return ResponseEntity.badRequest().body(respuesta);
    }

    //FastAPI está apagado o no se pudo establecer la conexión.
    @ExceptionHandler(DataScienceNoDisponibleException.class)
    public ResponseEntity<ErrorResponse> gestionarDataScienceNoDisponible(DataScienceNoDisponibleException exception){
        var respuesta = new ErrorResponse(
                "DATA_SCIENCE_NO_DISPONIBLE",
                exception.getMessage()
        );

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(respuesta);
    }

    // FastAPI sí respondió, pero devolvió un error o respuesta no compatible
    @ExceptionHandler(RespuestaDataScienceException.class)
    public ResponseEntity<ErrorResponse>  gestionarRespuestaDataScience(RespuestaDataScienceException exception){
        var respuesta = new ErrorResponse(
                "RESPUESTA_DATA_SCIENCE_INVALIDA",
                exception.getMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(respuesta);
    }

    //No debe revelar el mensaje interno de la excepción al cliente.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> gestionarError500(Exception exception) {
        logger.error("Ocurrió un error inesperado en el backend", exception);

        var respuesta = new ErrorResponse(
                "ERROR_INTERNO",
                "Ocurrió un error interno inesperado"
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
    }


    public record ErrorResponse(String codigo, String mensaje) {
    }

    public record DatosFaltantesError(String campo, String mensaje){
        public DatosFaltantesError(FieldError error){
            this(error.getField(), error.getDefaultMessage());
        }
    }
}


