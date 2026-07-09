package librosystemrr.modelos;

import java.io.Serializable;
import librosystemrr.tads.Cola;

/**
 * Representa una sala de lectura de la biblioteca.
 * Gestiona su disponibilidad y la cola de usuarios en espera usando el TAD {@link Cola}.
 *
 * <p>Cuando una sala está ocupada, las solicitudes se encolan (FIFO).
 * Al liberarse, la siguiente solicitud en espera se asigna automáticamente.</p>
 */
public class SalaLectura implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Identificador único de la sala (ej. "S01"). */
    private String id;

    /** Número visible de la sala para usuarios y reportes. */
    private int numero;

    /** Nombre descriptivo de la sala. */
    private String nombre;

    /** Capacidad máxima de personas. */
    private int capacidad;

    /** Indica si la sala está actualmente ocupada. */
    private boolean ocupada;

    /** Solicitud activa en esta sala. {@code null} si está libre. */
    private SolicitudSala solicitudActual;

    /**
     * Cola de solicitudes en espera para esta sala (TAD propio, FIFO).
     */
    private Cola<SolicitudSala> colaEspera;

    /** Contador estático para asignar número automáticamente. */
    private static int contadorNumero = 1;

    /**
     * Construye una sala de lectura disponible con cola de espera vacía.
     *
     * @param id        Identificador único.
     * @param nombre    Nombre descriptivo de la sala.
     * @param capacidad Capacidad máxima de personas.
     */
    public SalaLectura(String id, String nombre, int capacidad) {
        this.id = id;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.numero = contadorNumero++;
        this.ocupada = false;
        this.solicitudActual = null;
        this.colaEspera = new Cola<>();
    }

    // ══════════════════════════════════════════
    // MÉTODOS DE NEGOCIO
    // ══════════════════════════════════════════

    /**
     * Asigna esta sala a una solicitud.
     * Marca la sala como ocupada y actualiza la solicitud a estado ASIGNADA.
     *
     * @param solicitud La {@link SolicitudSala} que ocupará la sala.
     */
    public void asignar(SolicitudSala solicitud) {
        this.ocupada = true;
        this.solicitudActual = solicitud;
        solicitud.setEstado("ASIGNADA");
    }

    /**
     * Libera la sala. Si hay solicitudes en la cola interna, asigna la siguiente.
     * Marca la solicitud actual como FINALIZADA.
     *
     * @return La siguiente {@link SolicitudSala} asignada, o {@code null} si no hay espera.
     */
    public SolicitudSala liberar() {
        if (solicitudActual != null) {
            solicitudActual.setEstado("FINALIZADA");
        }
        if (!colaEspera.estaVacia()) {
            SolicitudSala siguiente = colaEspera.desencolar();
            asignar(siguiente);
            return siguiente;
        }
        ocupada = false;
        solicitudActual = null;
        return null;
    }

    /**
     * Agrega una solicitud a la cola de espera interna de esta sala.
     *
     * @param solicitud Solicitud a encolar.
     */
    public void encolarEspera(SolicitudSala solicitud) {
        colaEspera.encolar(solicitud);
    }

    // ══════════════════════════════════════════
    // GETTERS
    // ══════════════════════════════════════════

    /** @return Identificador de la sala. */
    public String getId() { return id; }

    /** @return Número visible de la sala. */
    public int getNumero() { return numero; }

    /** @return Nombre descriptivo de la sala. */
    public String getNombre() { return nombre; }

    /** @return Capacidad máxima de personas. */
    public int getCapacidad() { return capacidad; }

    /** @return {@code true} si la sala está ocupada. */
    public boolean isOcupada() { return ocupada; }

    /** @return Solicitud activa en la sala, o {@code null} si está libre. */
    public SolicitudSala getSolicitudActual() { return solicitudActual; }

    /** @return Cola interna de solicitudes en espera para esta sala. */
    public Cola<SolicitudSala> getColaEspera() { return colaEspera; }

    @Override
    public String toString() {
        return "SalaLectura{id='" + id + "', nombre='" + nombre +
                "', capacidad=" + capacidad + ", ocupada=" + ocupada + "}";
    }
}