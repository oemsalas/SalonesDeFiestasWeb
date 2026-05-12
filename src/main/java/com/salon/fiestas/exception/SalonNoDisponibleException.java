package com.salon.fiestas.exception;

public class SalonNoDisponibleException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public SalonNoDisponibleException(String motivo) {
        super("Salón no disponible: " + motivo);
    }
}
