package librosystemrr.modelos;

import java.util.Date;

/**
 * Representa un préstamo de un libro a un usuario.
 * Gestiona las fechas, el estado de devolución y la multa asociada si hay retraso.
 */
import java.io.Serializable;

public class Prestamo implements Serializable {

    private static final long serialVersionUID = 2L;

    /** Identificador único del préstamo. */
    private String id;

    /** Libro prestado. */
    private Libro libro;

    /** Usuario que solicitó el préstamo. */
    private Usuario usuario;

    /** Fecha en que se realizó el préstamo. */
    private Date fechaPrestamo;

    /** Fecha límite de devolución (14 días por defecto desde el préstamo). */
    private Date fechaDevolucion;

    /** Indica si el libro ya fue devuelto. */
    private boolean devuelto;

    /** Multa generada al devolver con retraso. {@code null} si no hay retraso o no fue devuelto. */
    private Multa multa;

    /** Días de préstamo por defecto. */
    private static final int DIAS_PRESTAMO = 14;

    /**
     * Construye un préstamo. La fecha de devolución se fija automáticamente
     * a 14 días desde hoy.
     *
     * @param id      Identificador único del préstamo.
     * @param libro   Libro a prestar.
     * @param usuario Usuario que solicita el préstamo.
     */
    public Prestamo(String id, Libro libro, Usuario usuario) {
        this.id = id;
        this.libro = libro;
        this.usuario = usuario;
        this.fechaPrestamo = new Date();
        // Fecha de devolución = hoy + 14 días (en milisegundos)
        this.fechaDevolucion = new Date(fechaPrestamo.getTime() + (long) DIAS_PRESTAMO * 24 * 60 * 60 * 1000);
        this.devuelto = false;
        this.multa = null;
    }

    // ══════════════════════════════════════════
    // MÉTODOS DE NEGOCIO
    // ══════════════════════════════════════════

    /**
     * Indica si el préstamo está vencido (fecha actual supera la fecha de devolución
     * y el libro aún no fue devuelto).
     *
     * @return {@code true} si el préstamo está vencido.
     */
    public boolean isVencido() {
        if (devuelto) return false;
        return new Date().after(fechaDevolucion);
    }

    /**
     * Calcula los días de retraso desde la fecha de devolución hasta hoy.
     * Si no hay retraso o el libro ya fue devuelto a tiempo, retorna 0.
     *
     * @return Número de días de retraso (0 si no hay retraso).
     */
    public int getDiasRetraso() {
        if (!isVencido()) return 0;
        long diffMs = new Date().getTime() - fechaDevolucion.getTime();
        return (int) (diffMs / (1000L * 60 * 60 * 24));
    }

    /**
     * Marca el préstamo como devuelto y genera una multa si hay días de retraso.
     * También actualiza la disponibilidad del libro a {@code true}.
     */
    public void devolver() {
        this.devuelto = true;
        this.libro.setDisponible(true);
        int diasRetraso = getDiasRetraso();
        if (diasRetraso > 0) {
            this.multa = new Multa(diasRetraso);
        }
    }

    // ══════════════════════════════════════════
    // GETTERS
    // ══════════════════════════════════════════

    /** @return Identificador del préstamo. */
    public String getId() { return id; }

    /** @return Libro prestado. */
    public Libro getLibro() { return libro; }

    /** @return Usuario que tiene el préstamo. */
    public Usuario getUsuario() { return usuario; }

    /** @return Fecha en que se realizó el préstamo. */
    public Date getFechaPrestamo() { return fechaPrestamo; }

    /** @return Fecha límite de devolución. */
    public Date getFechaDevolucion() { return fechaDevolucion; }

    /** @return {@code true} si el libro ya fue devuelto. */
    public boolean isDevuelto() { return devuelto; }

    /** @return Multa del préstamo, o {@code null} si no hay multa. */
    public Multa getMulta() { return multa; }

    /**
     * Permite establecer la fecha de devolución manualmente (útil para pruebas
     * con fechas pasadas que simulen vencimiento).
     *
     * @param fechaDevolucion Nueva fecha límite de devolución.
     */
    public void setFechaDevolucion(Date fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    @Override
    public String toString() {
        return "Prestamo{id='" + id + "', libro='" + libro.getTitulo() +
                "', usuario='" + usuario.getNombre() +
                "', vencido=" + isVencido() +
                ", devuelto=" + devuelto + "}";
    }
}