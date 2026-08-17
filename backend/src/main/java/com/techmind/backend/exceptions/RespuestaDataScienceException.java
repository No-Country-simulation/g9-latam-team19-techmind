package com.techmind.backend.exceptions;

public class RespuestaDataScienceException extends RuntimeException {
    public RespuestaDataScienceException(String mensaje) {
        super(mensaje);
    }

    public RespuestaDataScienceException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
