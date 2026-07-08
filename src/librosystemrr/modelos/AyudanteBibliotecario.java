package librosystemrr.modelos;

/**
 * Representa a un ayudante de bibliotecario. Extiende {@link Usuario}
 * con permisos limitados: puede registrar préstamos y procesar devoluciones,
 * pero no registrar nuevos libros.
 */
import java.io.Serializable;

public class AyudanteBibliotecario extends Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Código interno del empleado. */
    private String codigoEmpleado;

    /**
     * Construye un ayudante con su ID, nombre y código de empleado.
     *
     * @param id             Identificador único.
     * @param nombre         Nombre completo.
     * @param codigoEmpleado Código interno del empleado.
     */
    public AyudanteBibliotecario(String id, String nombre, String codigoEmpleado, String contrasena) {
        super(id, nombre, contrasena);
        this.codigoEmpleado = codigoEmpleado;
    }

    @Override
    public String getTipo() {
        return "AYUDANTE";
    }

    /** @return Código interno del empleado. */
    public String getCodigoEmpleado() { return codigoEmpleado; }

    // ══════════════════════════════════════════
    // MÉTODOS DE NEGOCIO
    // ══════════════════════════════════════════

    /**
     * Registra un préstamo delegando al SistemaBiblioteca.
     * El Ayudante puede registrar préstamos pero no agregar libros al catálogo.
     *
     * @param sistema   SistemaBiblioteca donde se registrará el préstamo.
     * @param idUsuario ID del usuario que solicita el préstamo.
     * @param isbn      ISBN del libro a prestar.
     * @return El {@link Prestamo} registrado.
     */
    public Prestamo registrarPrestamo(librosystemrr.sistema.SistemaBiblioteca sistema,
                                      String idUsuario, String isbn) {
        return sistema.registrarPrestamo(idUsuario, isbn);
    }

    /**
     * Procesa la devolución de un préstamo delegando al SistemaBiblioteca.
     *
     * @param sistema    SistemaBiblioteca donde se procesará la devolución.
     * @param idPrestamo ID del préstamo a devolver.
     * @return El {@link Prestamo} procesado (con multa si aplica).
     */
    public Prestamo procesarDevolucion(librosystemrr.sistema.SistemaBiblioteca sistema,
                                       String idPrestamo) {
        return sistema.procesarDevolucion(idPrestamo);
    }

    @Override
    public String toString() {
        return "AyudanteBibliotecario{id='" + getId() + "', nombre='" + getNombre() +
                "', codigo='" + codigoEmpleado + "'}";
    }
}