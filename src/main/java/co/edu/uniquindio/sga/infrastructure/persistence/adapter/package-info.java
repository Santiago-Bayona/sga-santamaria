/**
 * Implementación JPA de los puertos de repositorio del dominio.
 * La clase abre una transacción de solo lectura para poder leer colecciones perezosas durante el mapeo.
 * Cada {@code guardar} abre una transacción de escritura.
 */
package co.edu.uniquindio.sga.infrastructure.persistence.adapter;
