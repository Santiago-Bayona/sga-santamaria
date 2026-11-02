package co.edu.uniquindio.sga.application.exception;

public class NovedadYaRegistradaException extends RuntimeException {

    public NovedadYaRegistradaException(String codigo) {
        super("Ya existe una novedad con el código " + codigo);
    }
}