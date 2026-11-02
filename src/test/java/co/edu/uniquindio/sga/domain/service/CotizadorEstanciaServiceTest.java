package co.edu.uniquindio.sga.domain.service;

import co.edu.uniquindio.sga.domain.entity.Alojamiento;
import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.soporte.*;
import co.edu.uniquindio.sga.domain.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static co.edu.uniquindio.sga.domain.soporte.DatosDePrueba.*;
import static org.junit.jupiter.api.Assertions.*;

/** 7.4 (valor por ocupante facturable por noche, por temporada), RN-05 (tarifa de la temporada) y RN-06 (quién factura). */
class CotizadorEstanciaServiceTest {

    private Alojamiento alojamiento;
    private Apartamento apartamento;
    private CotizadorEstanciaService cotizador;

    @BeforeEach
    void preparar() {
        alojamiento = DatosDePrueba.alojamiento();
        apartamento = DatosDePrueba.apartamento("APT-301", 5);
        cotizador = new CotizadorEstanciaService(new ApartamentoRepositoryEnMemoria().con(apartamento),
                new AlojamientoRepositoryEnMemoria(alojamiento));
    }

    @Test
    @DisplayName("7.4: valor = noches × tarifa de la temporada × ocupantes facturables (el menor sin cargo no suma)")
    void cotizarPorOcupanteFacturablePorNoche() {
        LocalDate entrada = LocalDate.of(2026, 9, 10);                              // temporada BASE
        List<Ocupante> grupo = List.of(titular().comoOcupante(), adulto("Luis"), conEdadEn("Pedro", 2, entrada));

        Cotizacion cotizacion = cotizador.cotizar(apartamento, alojamiento, estancia(entrada, 3), grupo);

        assertEquals(3, cotizacion.numeroNoches());
        assertTrue(cotizacion.desglose().stream().allMatch(d -> d.ocupantesFacturables() == 2));
        assertEquals(Dinero.pesos(3 * 2 * 100_000), cotizacion.total());
    }

    @Test
    @DisplayName("7.4/RN-05: cada noche usa la tarifa de su temporada (el cambio de temporada se refleja en el desglose)")
    void aplicarLaTarifaDeLaTemporadaDeCadaNoche() {
        LocalDate entrada = LocalDate.of(2026, 7, 30);                              // 30 y 31 jul: MEDIA; 1 ago: BASE
        List<Ocupante> grupo = List.of(titular().comoOcupante());

        Cotizacion cotizacion = cotizador.cotizar(apartamento, alojamiento, estancia(entrada, 3), grupo);

        assertEquals(TEMPORADA_MEDIA, cotizacion.desglose().get(0).temporada());
        assertEquals(TEMPORADA_MEDIA, cotizacion.desglose().get(1).temporada());
        assertEquals(TEMPORADA_BASE, cotizacion.desglose().get(2).temporada());
        assertEquals(Dinero.pesos(150_000 + 150_000 + 100_000), cotizacion.total());
    }

    @Test
    @DisplayName("7.4: la temporada que cruza el año (15 dic – 10 ene) usa su propia tarifa")
    void cotizarEstanciaQueCruzaElAnio() {
        LocalDate entrada = LocalDate.of(2026, 12, 31);

        Cotizacion cotizacion = cotizador.cotizar(apartamento, alojamiento, estancia(entrada, 2),
                List.of(titular().comoOcupante()));

        assertEquals(Dinero.pesos(2 * 200_000), cotizacion.total());
    }

    @Test
    @DisplayName("RN-06: si ningún ocupante es facturable no se cotiza")
    void noCotizarGrupoSinOcupantesFacturables() {
        LocalDate entrada = LocalDate.of(2026, 9, 10);

        assertThrows(ReglaDominioException.class, () -> cotizador.cotizar(apartamento, alojamiento,
                estancia(entrada, 2), List.of(conEdadEn("Pedro", 2, entrada))));
        assertThrows(ReglaDominioException.class, () -> cotizador.cotizar(apartamento, alojamiento,
                estancia(entrada, 2), List.of()));
    }

    @Test
    @DisplayName("7.4: si el apartamento no tiene tarifa para la temporada de alguna noche, no se cotiza")
    void noCotizarSinTarifaParaLaTemporada() {
        Apartamento sinTarifas = apartamentoSinTarifas("APT-999", 4);
        sinTarifas.definirTarifa(new Tarifa(TEMPORADA_BASE, TARIFA_BASE), AHORA);

        assertThrows(ReglaDominioException.class, () -> cotizador.cotizar(sinTarifas, alojamiento,
                estancia(LocalDate.of(2026, 7, 10), 2), List.of(titular().comoOcupante())));
    }

    @Test
    @DisplayName("7.4: cotizar por identificación falla si el apartamento no existe")
    void noCotizarApartamentoInexistente() {
        assertThrows(ReglaDominioException.class, () -> cotizador.cotizar(new IdentificacionApartamento("NO-EXISTE"),
                estancia(LocalDate.of(2026, 9, 10), 2), List.of(titular().comoOcupante())));
        assertEquals(Dinero.pesos(200_000), cotizador.cotizar(apartamento.getIdentificacion(),
                estancia(LocalDate.of(2026, 9, 10), 2), List.of(titular().comoOcupante())).total());
    }
}
