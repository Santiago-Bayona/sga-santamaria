package co.edu.uniquindio.sga.application.dto.response;

public record ApartamentoDisponibleResponse(
        String identificacion,
        String nombre,
        int capacidad,
        int dormitorios,
        String imagenPrincipal,
        long valorEstimado
) {
}
