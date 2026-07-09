package librosystemrr.modelos;

import java.io.Serializable;
import java.util.Date;

/**
 * Representa una solicitud de uso de computadora en la biblioteca.
 * Se almacena en la {@link librosystemrr.tads.Cola} de espera del sistema
 * usando el TAD Cola (FIFO).
 */
public class SolicitudComputadora implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Identificador único de la solicitud. */
    private String id;

    /** ID del usuario solicitante. */
    private String idUsuario;

    /** Nombre del usuario solicitante. */
    private String nombreUsuario;

    /** Minutos de uso solicitados. */
    private int duracionMinutos;

    /** Fecha y hora en que se realizó la solicitud. */
    private Date fechaSolicitud;

    /** Estado de la solicitud: ESPERA, ASIGNADA, FINALIZADA. */
    private String estado;

    /**
     * Construye una nueva solicitud de computadora.
     *
     * @param id              Identificador único.
     * @param idUsuario       ID del usuario solicitante.
     * @param nombreUsuario   Nombre completo del usuario.
     * @param duracionMinutos Minutos de uso solicitados.
     */
    public SolicitudComputadora(String id, String idUsuario, String nombreUsuario, int duracionMinutos) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.duracionMinutos = duracionMinutos;
        this.fechaSolicitud = new Date();
        this.estado = "ESPERA";
    }

    /** @return Identificador de la solicitud. */
    public String getId() { return id; }

    /** @return ID del usuario solicitante. */
    public String getIdUsuario() { return idUsuario; }

    /** @return Nombre del usuario solicitante. */
    public String getNombreUsuario() { return nombreUsuario; }

    /** @return Minutos de uso solicitados. */
    public int getDuracionMinutos() { return duracionMinutos; }

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
        return "SolicitudComputadora{id='" + id + "', usuario='" + nombreUsuario +
                "', duracion=" + duracionMinutos + "min, estado=" + estado + "}";
    }
}