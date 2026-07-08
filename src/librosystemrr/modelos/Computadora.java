package librosystemrr.modelos;

import java.io.Serializable;

/**
 * Representa una computadora disponible para uso en la biblioteca.
 * Gestiona su disponibilidad y la solicitud activa.
 * La cola de espera general se maneja en {@link librosystemrr.sistema.SistemaBiblioteca}.
 */
public class Computadora implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Identificador único. */
    private String id;

    /** Número visible de la computadora en la sala. */
    private int numero;

    /** Indica si la computadora está disponible. */
    private boolean disponible;

    /** Solicitud activa. {@code null} si está disponible. */
    private SolicitudComputadora solicitudActual;

    /**
     * Construye una computadora disponible.
     *
     * @param id     Identificador único.
     * @param numero Número visible de la computadora.
     */
    public Computadora(String id, int numero) {
        this.id = id;
        this.numero = numero;
        this.disponible = true;
        this.solicitudActual = null;
    }

    // ══════════════════════════════════════════
    // MÉTODOS DE NEGOCIO
    // ══════════════════════════════════════════

    /**
     * Asigna esta computadora a una solicitud.
     * Marca la computadora como no disponible y la solicitud como ASIGNADA.
     *
     * @param solicitud La {@link SolicitudComputadora} que usará la computadora.
     */
    public void asignar(SolicitudComputadora solicitud) {
        this.disponible = false;
        this.solicitudActual = solicitud;
        solicitud.setEstado("ASIGNADA");
    }

    /**
     * Libera la computadora y marca la solicitud actual como FINALIZADA.
     */
    public void liberar() {
        if (solicitudActual != null) {
            solicitudActual.setEstado("FINALIZADA");
            solicitudActual = null;
        }
        this.disponible = true;
    }

    // ══════════════════════════════════════════
    // GETTERS
    // ══════════════════════════════════════════

    /** @return Identificador de la computadora. */
    public String getId() { return id; }

    /** @return Número visible de la computadora. */
    public int getNumero() { return numero; }

    /** @return {@code true} si la computadora está disponible. */
    public boolean isDisponible() { return disponible; }

    /** @return Solicitud activa, o {@code null} si está disponible. */
    public SolicitudComputadora getSolicitudActual() { return solicitudActual; }

    @Override
    public String toString() {
        return "Computadora{id='" + id + "', numero=" + numero +
                ", disponible=" + disponible + "}";
    }
}