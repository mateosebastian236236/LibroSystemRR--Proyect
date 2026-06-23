package librosystemrr.sistema;

import librosystemrr.algoritmos.AlgoritmosBusqueda;
import librosystemrr.algoritmos.AlgoritmosOrdenamiento;
import librosystemrr.excepciones.CatalogoVacioException;
import librosystemrr.excepciones.LibroNoDisponibleException;
import librosystemrr.excepciones.LibroNoEncontradoException;
import librosystemrr.excepciones.UsuarioConDeudaException;
import librosystemrr.excepciones.UsuarioNoEncontradoException;
import librosystemrr.modelos.Libro;
import librosystemrr.modelos.Lector;
import librosystemrr.modelos.Prestamo;
import librosystemrr.modelos.Usuario;
import librosystemrr.tads.ListaEnlazada;
import librosystemrr.tads.arbol.CatalogoBST;
import librosystemrr.util.SistemaLogger;

/**
 * Capa de negocio central del sistema LibroSystemRR.
 * Coordina TADs, algoritmos y modelos. La UI solo interactúa con esta clase.
 *
 * <p>Principio SRP: esta clase es la única responsable de orquestar las
 * operaciones del sistema. No conoce la existencia de la UI.</p>
 *
 * <p>Principio DIP: depende de las abstracciones {@code Buscable} y {@code Ordenable},
 * no de implementaciones concretas de librerías externas.</p>
 */
public class SistemaBiblioteca {

    /** Catálogo de libros organizado en BST (búsqueda O(log n) por ISBN). */
    private CatalogoBST catalogo;

    /** Lista de todos los usuarios registrados (búsqueda lineal O(n) por ID). */
    private ListaEnlazada<Usuario> usuarios;

    /** Lista de todos los préstamos registrados en el sistema. */
    private ListaEnlazada<Prestamo> prestamos;

    /** Logger singleton para registrar operaciones del sistema. */
    private SistemaLogger logger;

    /** Contador para generar IDs únicos de préstamo. */
    private int contadorPrestamos;

    /**
     * Construye el sistema de biblioteca con todas las estructuras vacías.
     */
    public SistemaBiblioteca() {
        this.catalogo = new CatalogoBST();
        this.usuarios = new ListaEnlazada<>();
        this.prestamos = new ListaEnlazada<>();
        this.logger = SistemaLogger.getInstancia();
        this.contadorPrestamos = 1;
    }

    // ══════════════════════════════════════════════════════════════
    // MÓDULO DE CATÁLOGO
    // ══════════════════════════════════════════════════════════════

    /**
     * Registra un libro en el catálogo (lo inserta en el BST). O(log n) prom.
     *
     * @param libro Libro a registrar. No debe ser {@code null}.
     */
    public void registrarLibro(Libro libro) {
        catalogo.insertar(libro);
        logger.registrarInfo("Libro registrado: " + libro.getIsbn() + " — " + libro.getTitulo());
    }

    /**
     * Busca un libro por ISBN usando el BST. O(log n) prom.
     *
     * @param isbn ISBN exacto del libro.
     * @return El {@link Libro} encontrado.
     * @throws LibroNoEncontradoException si el ISBN no existe en el catálogo.
     */
    public Libro buscarLibroPorIsbn(String isbn) {
        Libro resultado = AlgoritmosBusqueda.buscarEnBST(catalogo.getRaiz(), isbn);
        if (resultado == null) {
            throw new LibroNoEncontradoException(isbn);
        }
        return resultado;
    }

    /**
     * Busca libros cuyo título contenga la cadena indicada. O(n).
     *
     * @param titulo Fragmento del título a buscar.
     * @return Lista de libros que coinciden (puede estar vacía).
     * @throws CatalogoVacioException si el catálogo está vacío.
     */
    public ListaEnlazada<Libro> buscarLibroPorTitulo(String titulo) {
        return catalogo.buscarPorTitulo(titulo);
    }

    // ══════════════════════════════════════════════════════════════
    // MÓDULO DE USUARIOS
    // ══════════════════════════════════════════════════════════════

    /**
     * Registra un usuario en el sistema. O(1) al inicio de la lista.
     *
     * @param usuario Usuario a registrar.
     */
    public void registrarUsuario(Usuario usuario) {
        usuarios.agregar(usuario);
        logger.registrarInfo("Usuario registrado: " + usuario.getId() + " — " + usuario.getNombre());
    }

    /**
     * Busca un usuario por ID usando búsqueda lineal. O(n).
     *
     * @param id Identificador del usuario.
     * @return El {@link Usuario} encontrado.
     * @throws UsuarioNoEncontradoException si el ID no existe.
     */
    public Usuario buscarUsuario(String id) {
        return AlgoritmosBusqueda.busquedaLinealUsuario(usuarios, id);
    }

    // ══════════════════════════════════════════════════════════════
    // MÓDULO DE PRÉSTAMOS
    // ══════════════════════════════════════════════════════════════

    /**
     * Registra un préstamo con todas las validaciones de negocio.
     * Valida: libro disponible, usuario tipo Lector, no excede límite, no tiene deudas.
     *
     * @param idUsuario ID del usuario que solicita el préstamo.
     * @param isbn      ISBN del libro a prestar.
     * @return El {@link Prestamo} creado.
     * @throws UsuarioNoEncontradoException si el ID de usuario no existe.
     * @throws LibroNoEncontradoException   si el ISBN no existe en el catálogo.
     * @throws LibroNoDisponibleException   si el libro ya está prestado.
     * @throws UsuarioConDeudaException     si el lector tiene deudas pendientes.
     * @throws IllegalStateException        si el lector superó el límite de préstamos.
     */
    public Prestamo registrarPrestamo(String idUsuario, String isbn) {
        // 1. Buscar usuario y libro
        Usuario usuario = buscarUsuario(idUsuario);
        Libro libro = buscarLibroPorIsbn(isbn);

        // 2. Validar disponibilidad del libro
        if (!libro.isDisponible()) {
            throw new LibroNoDisponibleException(isbn);
        }

        // 3. Validaciones específicas de Lector
        if (usuario instanceof Lector) {
            Lector lector = (Lector) usuario;

            if (!lector.puedePrestar()) {
                throw new IllegalStateException(
                        "El lector " + idUsuario + " ya tiene " + Lector.LIMITE_PRESTAMOS +
                                " préstamos activos. No puede solicitar más.");
            }

            if (lector.tieneDeudas()) {
                throw new UsuarioConDeudaException(idUsuario);
            }
        }

        // 4. Crear préstamo, actualizar libro y registrar en estructuras
        String idPrestamo = "P" + String.format("%04d", contadorPrestamos++);
        Prestamo prestamo = new Prestamo(idPrestamo, libro, usuario);

        libro.setDisponible(false);
        usuario.getPrestamosActivos().encolar(prestamo);
        usuario.getHistorialPrestamos().apilar(prestamo);
        prestamos.agregar(prestamo);

        logger.registrarInfo("Préstamo registrado: " + idPrestamo +
                " | Usuario: " + idUsuario + " | Libro: " + isbn);
        return prestamo;
    }

    /**
     * Procesa la devolución de un préstamo. Genera multa automáticamente si hay retraso.
     *
     * @param idPrestamo ID del préstamo a devolver.
     * @return El {@link Prestamo} procesado (con multa si aplica).
     * @throws LibroNoEncontradoException si el ID de préstamo no existe.
     */
    public Prestamo procesarDevolucion(String idPrestamo) {
        // Buscar el préstamo en la lista general
        Prestamo prestamoEncontrado = null;
        for (int i = 0; i < prestamos.getTamanio(); i++) {
            Prestamo p = prestamos.obtener(i);
            if (p.getId().equals(idPrestamo) && !p.isDevuelto()) {
                prestamoEncontrado = p;
                break;
            }
        }

        if (prestamoEncontrado == null) {
            throw new LibroNoEncontradoException("Préstamo no encontrado o ya devuelto: " + idPrestamo);
        }

        // Procesar la devolución (actualiza disponibilidad y genera multa si aplica)
        prestamoEncontrado.devolver();

        // Desencolar de prestamosActivos del usuario
        prestamoEncontrado.getUsuario().getPrestamosActivos().desencolar();

        if (prestamoEncontrado.getMulta() != null) {
            logger.registrarWarning("Devolución con retraso. Multa generada: $" +
                    String.format("%.2f", prestamoEncontrado.getMulta().getMonto()) +
                    " | Préstamo: " + idPrestamo);
        } else {
            logger.registrarInfo("Devolución exitosa sin retraso. Préstamo: " + idPrestamo);
        }

        return prestamoEncontrado;
    }

    // ══════════════════════════════════════════════════════════════
    // MÓDULO DE ALERTAS Y ORDENAMIENTO
    // ══════════════════════════════════════════════════════════════

    /**
     * Genera una lista de todos los préstamos vencidos activos en el sistema. O(n).
     *
     * @return {@link ListaEnlazada} con los préstamos vencidos (puede estar vacía).
     */
    public ListaEnlazada<Prestamo> generarAlertas() {
        ListaEnlazada<Prestamo> vencidos = new ListaEnlazada<>();
        for (int i = 0; i < prestamos.getTamanio(); i++) {
            Prestamo p = prestamos.obtener(i);
            if (p.isVencido()) {
                vencidos.agregar(p);
                logger.registrarWarning("Préstamo vencido detectado: " + p.getId() +
                        " | Días retraso: " + p.getDiasRetraso());
            }
        }
        return vencidos;
    }

    /**
     * Ordena el catálogo de libros por título usando MergeSort. O(n log n).
     *
     * @return {@link ListaEnlazada} con todos los libros ordenados por título.
     * @throws CatalogoVacioException si el catálogo está vacío.
     */
    public ListaEnlazada<Libro> ordenarCatalogoPorTitulo() {
        ListaEnlazada<Libro> lista = catalogo.inorden(); // obtiene todos los libros
        AlgoritmosOrdenamiento.ordenarPorTitulo(lista);
        logger.registrarInfo("Catálogo ordenado por título.");
        return lista;
    }

    /**
     * Ordena el catálogo de libros por autor usando MergeSort. O(n log n).
     *
     * @return {@link ListaEnlazada} con todos los libros ordenados por autor.
     * @throws CatalogoVacioException si el catálogo está vacío.
     */
    public ListaEnlazada<Libro> ordenarCatalogoPorAutor() {
        ListaEnlazada<Libro> lista = catalogo.inorden();
        AlgoritmosOrdenamiento.ordenarPorAutor(lista);
        logger.registrarInfo("Catálogo ordenado por autor.");
        return lista;
    }

    // ══════════════════════════════════════════════════════════════
    // GETTERS (para uso interno y pruebas)
    // ══════════════════════════════════════════════════════════════

    /** @return El catálogo BST del sistema. */
    public CatalogoBST getCatalogo() { return catalogo; }

    /** @return Lista de todos los usuarios registrados. */
    public ListaEnlazada<Usuario> getUsuarios() { return usuarios; }

    /** @return Lista de todos los préstamos registrados. */
    public ListaEnlazada<Prestamo> getPrestamos() { return prestamos; }
}