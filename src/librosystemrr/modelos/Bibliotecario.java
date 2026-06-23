package librosystemrr.modelos;

/**
 * Representa a un bibliotecario de la institución. Extiende {@link Usuario}
 * y tiene permisos para registrar libros y procesar devoluciones.
 */
public class Bibliotecario extends Usuario {

    /** Código interno del empleado. */
    private String codigoEmpleado;

    /**
     * Construye un bibliotecario con su ID, nombre y código de empleado.
     *
     * @param id             Identificador único.
     * @param nombre         Nombre completo.
     * @param codigoEmpleado Código interno del empleado.
     */
    public Bibliotecario(String id, String nombre, String codigoEmpleado) {
        super(id, nombre);
        this.codigoEmpleado = codigoEmpleado;
    }

    @Override
    public String getTipo() {
        return "BIBLIOTECARIO";
    }

    /** @return Código interno del empleado. */
    public String getCodigoEmpleado() { return codigoEmpleado; }

    // ══════════════════════════════════════════
    // MÉTODOS DE NEGOCIO
    // ══════════════════════════════════════════

    /**
     * Registra un libro en el sistema delegando al SistemaBiblioteca.
     * El Bibliotecario es el único rol con permiso para agregar nuevos libros al catálogo.
     *
     * @param sistema SistemaBiblioteca donde se registrará el libro.
     * @param libro   Libro a registrar.
     */
    public void registrarLibro(librosystemrr.sistema.SistemaBiblioteca sistema, Libro libro) {
        sistema.registrarLibro(libro);
    }

    /**
     * Procesa la devolución de un préstamo delegando al SistemaBiblioteca.
     *
     * @param sistema    SistemaBiblioteca donde se procesará la devolución.
     * @param idPrestamo ID del préstamo a devolver.
     * @return El {@link Prestamo} procesado (con multa si aplica).
     */
    public Prestamo procesarDevolucion(librosystemrr.sistema.SistemaBiblioteca sistema, String idPrestamo) {
        return sistema.procesarDevolucion(idPrestamo);
    }

    /**
     * Genera un reporte de texto con el estado actual del sistema:
     * total de libros, usuarios y préstamos activos.
     *
     * @param sistema SistemaBiblioteca del que se extraerá la información.
     * @return Cadena con el reporte generado.
     */
    public String generarReporte(librosystemrr.sistema.SistemaBiblioteca sistema) {
        int totalLibros = sistema.getCatalogo().getTamanio();
        int totalUsuarios = sistema.getUsuarios().getTamanio();
        int totalPrestamos = sistema.getPrestamos().getTamanio();

        int prestamosActivos = 0;
        for (int i = 0; i < totalPrestamos; i++) {
            if (!sistema.getPrestamos().obtener(i).isDevuelto()) {
                prestamosActivos++;
            }
        }

        return "=== REPORTE DEL SISTEMA ===\n" +
                "Generado por: " + getNombre() + " (" + codigoEmpleado + ")\n" +
                "Libros en catálogo : " + totalLibros + "\n" +
                "Usuarios registrados: " + totalUsuarios + "\n" +
                "Préstamos activos   : " + prestamosActivos + " / " + totalPrestamos + "\n" +
                "===========================";
    }

    @Override
    public String toString() {
        return "Bibliotecario{id='" + getId() + "', nombre='" + getNombre() +
                "', codigo='" + codigoEmpleado + "'}";
    }
}