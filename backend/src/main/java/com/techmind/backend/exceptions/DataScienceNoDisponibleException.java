package com.techmind.backend.exceptions;

public class DataScienceNoDisponibleException extends RuntimeException {
    public DataScienceNoDisponibleException(String mensaje) {
        super(mensaje);
    }

    public DataScienceNoDisponibleException(
            String mensaje,
            Throwable causa
    ) {
        super(mensaje, causa);
    }
}