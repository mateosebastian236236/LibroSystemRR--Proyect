package librosystemrr.modelos;

import java.io.Serializable;
import java.util.Date;

/**
 * Representa una solicitud de uso de sala de lectura.
 * Se almacena en la {@link librosystemrr.tads.Cola} de espera de cada sala
 * y en la cola general del sistema usando el TAD Cola (FIFO).
 */
public class SolicitudSala implements Serializable {

    /** Identificador único de la solicitud. */
    private String id;

    /** ID del usuario solicitante. */
    private String idUsuario;

    /** Nombre del usuario solicitante. */
    private String nombreUsuario;

    /** Horas de uso solicitadas. */
    private int duracionHoras;

    /** Fecha y hora en que se realizó la solicitud. */
    private Date fechaSolicitud;

    /** Estado de la solicitud: ESPERA, ASIGNADA, FINALIZADA. */
    private String estado;

    /**
     * Construye una nueva solicitud de sala.
     *
     * @param id            Identificador único.
     * @param idUsuario     ID del usuario solicitante.
     * @param nombreUsuario Nombre completo del usuario.
     * @param duracionHoras Horas de uso solicitadas.
     */
    public SolicitudSala(String id, String idUsuario, String nombreUsuario, int duracionHoras) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.duracionHoras = duracionHoras;
        this.fechaSolicitud = new Date();
        this.estado = "ESPERA";
    }

    /** @return Identificador de la solicitud. */
    public String getId() { return id; }

    /** @return ID del usuario solicitante. */
    public String getIdUsuario() { return idUsuario; }

    /** @return Nombre del usuario solicitante. */
    public String getNombreUsuario() { return nombreUsuario; }

    /** @return Horas de uso solicitadas. */
    public int getDuracionHoras() { return duracionHoras; }

    /** @return Fecha en que se realizó la solicitud. */
    public Date getFechaSolicitud() { return fechaSolicitud; }

    /** @return Estado actual de la solicitud. */
    public String getEstado() { return estado; }

    /**
     * Actualiza el estado de la solicitud.
     *
     * @param estado Nuevo estado: "ESPERA", "ASIGNADA" o "FINALIZADA".
     */
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "SolicitudSala{id='" + id + "', usuario='" + nombreUsuario +
                "', duracion=" + duracionHoras + "h, estado=" + estado + "}";
    }
}