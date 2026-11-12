package co.edu.uniquindio.sga.domain.valueobject;

/**
 * Por qué un resumen no fue redactado por el modelo de IA (10.2). La IA es opcional: cualquiera de estos motivos
 * degrada el resumen a su versión básica, pero nunca impide operar.
 */
public enum MotivoDegradacionIa {

    /** La IA está apagada por configuración. */
    IA_DESHABILITADA,

    /** Se usó la implementación local simulada: no hay modelo detrás. */
    IA_SIMULADA,

    /** El modelo no respondió dentro del tiempo máximo. */
    TIEMPO_AGOTADO,

    /** El proveedor respondió con error, o la respuesta no sirvió. */
    ERROR_PROVEEDOR
}
