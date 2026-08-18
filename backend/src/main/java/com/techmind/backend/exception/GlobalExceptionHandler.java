package com.techmind.backend.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Error de validación en los DTOs Records (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarValidaciones(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
    }

    // 2. Maneja tanto el Timeout (504) como la inaccesibilidad/apagado de FastAPI (503)
    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<Map<String, String>> manejarFallaConexionOFastApiTimeout(ResourceAccessException ex) {
        Map<String, String> respuesta = new HashMap<>();

        // Si la causa interna fue haber superado el tiempo límite de lectura (Read Timeout)
        if (ex.getCause() instanceof SocketTimeoutException) {
            respuesta.put("error", "El servicio de procesamiento de IA (FastAPI) excedió el tiempo límite de respuesta.");
            respuesta.put("detalle", "La operación tardó más de 5 segundos en responder.");

            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(respuesta); // HTTP 504
        }

        // Si la causa no fue tiempo de espera, es porque el servidor de Python está apagado o inalcanzable
        respuesta.put("error", "El servicio de procesamiento de IA (FastAPI) no está disponible en este momento.");
        respuesta.put("detalle", "No se pudo establecer conexión con http://localhost:8000.");

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(respuesta); // HTTP 503
    }

    // 3. Agrupar 4xx y 5xx de FastAPI en un solo handler si buscas reducir código
    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<Map<String, String>> manejarErroresHttpFastApi(HttpStatusCodeException ex) {
        Map<String, String> respuesta = new HashMap<>();

        if (ex.getStatusCode().is4xxClientError()) {
            respuesta.put("error", "La solicitud fue rechazada por el servicio de IA.");
            respuesta.put("detalle", ex.getResponseBodyAsString());
            return ResponseEntity.status(ex.getStatusCode()).body(respuesta);
        }

        respuesta.put("error", "El servicio de IA sufrió un error interno al procesar el texto.");
        respuesta.put("detalle", ex.getResponseBodyAsString());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(respuesta); // HTTP 502
    }

    // 4. Intercepta cuando un recurso no existe en la base de datos
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> manejarRecursoNoEncontrado(ResourceNotFoundException ex) {
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("error", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta); // HTTP 404
    }

    // 5. Captura cuando el JSON de la petición está mal construido o tiene tipos incoherentes
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> manejarJsonInvalido(HttpMessageNotReadableException ex) {
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("error", "El cuerpo de la petición (JSON) está mal formado o contiene tipos de datos inválidos.");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta); // HTTP 400
    }

    // 6. Captura cuando el parámetro en la URL no coincide con el tipo esperado (ej: texto donde va un ID numérico)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> manejarTipoParametroIncorrecto(MethodArgumentTypeMismatchException ex) {
        Map<String, String> respuesta = new HashMap<>();
        String tipoEsperado = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "válido";

        respuesta.put("error", String.format("El parámetro '%s' debe ser de tipo %s.", ex.getName(), tipoEsperado));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta); // HTTP 400
    }

    // 7. Captura cuando se intenta usar un verbo HTTP no permitido en el endpoint (ej: POST en un /id)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> manejarMetodoNoSoportado(HttpRequestMethodNotSupportedException ex) {
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("error", String.format("El método HTTP '%s' no está permitido para este endpoint.", ex.getMethod()));

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(respuesta); // HTTP 405
    }

    // 8. Captura violaciones de restricciones en la Base de Datos (ej: UNIQUE, FK o longitud excesiva)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> manejarViolacionIntegridadBD(DataIntegrityViolationException ex) {
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("error", "Conflicto con el estado actual del recurso: la operación viola una restricción en la base de datos.");

        return ResponseEntity.status(HttpStatus.CONFLICT).body(respuesta); // HTTP 409
    }

    // 9. Base de Datos Inalcanzable / Caída del Pool (HikariCP)
    @ExceptionHandler(CannotCreateTransactionException.class)
    public ResponseEntity<Map<String, String>> manejarFallaConexionBD(CannotCreateTransactionException ex) {
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("error", "El servicio de base de datos no está disponible en este momento.");
        respuesta.put("detalle", "No se pudo establecer conexión con el servidor MySQL.");

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(respuesta); // HTTP 503
    }

    // 10. Validaciones en @PathVariable o @RequestParam
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> manejarValidacionParametros(ConstraintViolationException ex) {
        Map<String, String> errores = new HashMap<>();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            // Extrae el nombre del parámetro (ej. "obtenerPorId.id" -> "id")
            String campo = violation.getPropertyPath().toString();
            if (campo.contains(".")) {
                campo = campo.substring(campo.lastIndexOf('.') + 1);
            }
            errores.put(campo, violation.getMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores); // HTTP 400
    }

    // 11. Captura general de respaldo para cualquier error no controlado
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> manejarExcepcionesInesperadas(Exception ex) {
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("error", "Ocurrió un error interno e inesperado en el servidor.");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta); // HTTP 500
    }
}
