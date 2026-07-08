package librosystemrr.sistema;

import librosystemrr.algoritmos.AlgoritmosBusqueda;
import librosystemrr.modelos.Computadora;
import librosystemrr.modelos.SalaLectura;
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
import librosystemrr.modelos.SolicitudComputadora;
import librosystemrr.modelos.SolicitudSala;
import librosystemrr.tads.Cola;
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
import java.io.Serializable;

public class SistemaBiblioteca implements Serializable {

    /** Catálogo de libros organizado en BST (búsqueda O(log n) por ISBN). */
    private CatalogoBST catalogo;

    /** Lista de todos los usuarios registrados (búsqueda lineal O(n) por ID). */
    private ListaEnlazada<Usuario> usuarios;

    /** Lista de todos los préstamos registrados en el sistema. */
    private ListaEnlazada<Prestamo> prestamos;

    /** Logger singleton para registrar operaciones del sistema. */
    private SistemaLogger logger;

    /** Lista de salas de lectura registradas. */
    private ListaEnlazada<SalaLectura> salas;

    /** Lista de computadoras registradas. */
    private ListaEnlazada<Computadora> computadoras;

    /** Contador para generar IDs únicos de préstamo. */
    private int contadorPrestamos;
    /** Lista de salas de lectura disponibles. */

    /** Cola general de espera para salas (cuando todas están ocupadas). */
    private Cola<SolicitudSala> colaSalas;

    /** Lista de computadoras de la biblioteca. */

    /** Cola general de espera para computadoras. */
    private Cola<SolicitudComputadora> colaComputadoras;

    /** Contador para IDs únicos de solicitudes de sala. */
    private int contadorSolicitudesSalas;

    /** Contador para IDs únicos de solicitudes de computadora. */
    private int contadorSolicitudesComputadoras;


    /**
     * Construye el sistema de biblioteca con todas las estructuras vacías.
     */
    public SistemaBiblioteca() {
        this.catalogo = new CatalogoBST();
        this.usuarios = new ListaEnlazada<>();
        this.prestamos = new ListaEnlazada<>();
        this.logger = SistemaLogger.getInstancia();
        this.salas = new ListaEnlazada<>();
        this.computadoras = new ListaEnlazada<>();
        this.contadorPrestamos = 1;
        this.salas = new ListaEnlazada<>();
        this.colaSalas = new Cola<>();
        this.computadoras = new ListaEnlazada<>();
        this.colaComputadoras = new Cola<>();
        this.contadorSolicitudesSalas = 1;
        this.contadorSolicitudesComputadoras = 1;
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

        // Remover el préstamo específico de la cola de activos del usuario.
        // Como Cola<T> es FIFO sin acceso por ID, vaciamos y re-encolamos
        // solo los préstamos que NO sean el devuelto.
        Cola<Prestamo> colaActual = prestamoEncontrado.getUsuario().getPrestamosActivos();
        int totalEnCola = colaActual.getTamanio();
        Prestamo[] temporal = new Prestamo[totalEnCola];
        for (int i = 0; i < totalEnCola; i++) {
            temporal[i] = colaActual.desencolar();
        }
        for (int i = 0; i < totalEnCola; i++) {
            if (!temporal[i].getId().equals(idPrestamo)) {
                colaActual.encolar(temporal[i]);
            }
        }

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
    // MÓDULO DE SALAS DE LECTURA
    // ══════════════════════════════════════════════════════════════

    /**
     * Registra una sala de lectura en el sistema. O(1).
     *
     * @param sala Sala a registrar.
     */
    public void registrarSala(SalaLectura sala) {
        salas.agregar(sala);
        logger.registrarInfo("Sala registrada: " + sala.getId() + " - " + sala.getNombre());
    }

    /**
     * Solicita una sala de lectura para un usuario.
     * Si hay una sala libre, la asigna directamente.
     * Si todas estan ocupadas, encola la solicitud (FIFO usando Cola).
     *
     * @param idUsuario     ID del usuario solicitante.
     * @param nombreUsuario Nombre del usuario.
     * @param duracionHoras Horas de uso solicitadas.
     * @return La {@link SolicitudSala} creada.
     */
    public SolicitudSala solicitarSala(String idUsuario, String nombreUsuario, int duracionHoras) {
        String idSol = "RS" + String.format("%04d", contadorSolicitudesSalas++);
        SolicitudSala solicitud = new SolicitudSala(idSol, idUsuario, nombreUsuario, duracionHoras);

        for (int i = 0; i < salas.getTamanio(); i++) {
            SalaLectura sala = salas.obtener(i);
            if (!sala.isOcupada()) {
                sala.asignar(solicitud);
                logger.registrarInfo("Sala " + sala.getNumero() + " asignada a " + nombreUsuario);
                return solicitud;
            }
        }
        colaSalas.encolar(solicitud);
        logger.registrarWarning("Todas las salas ocupadas. " + nombreUsuario + " en lista de espera.");
        return solicitud;
    }

    /**
     * Libera una sala de lectura por su ID.
     * Si hay solicitudes en espera, asigna la sala automaticamente.
     *
     * @param idSala ID de la sala a liberar.
     */
    public void liberarSala(String idSala) {
        for (int i = 0; i < salas.getTamanio(); i++) {
            SalaLectura sala = salas.obtener(i);
            if (sala.getId().equals(idSala)) {
                SolicitudSala siguiente = sala.liberar();
                if (siguiente != null) {
                    logger.registrarInfo("Sala " + sala.getNumero() + " reasignada a " + siguiente.getNombreUsuario());
                } else if (!colaSalas.estaVacia()) {
                    SolicitudSala proxima = colaSalas.desencolar();
                    sala.asignar(proxima);
                    logger.registrarInfo("Sala " + sala.getNumero() + " asignada desde cola general a " + proxima.getNombreUsuario());
                } else {
                    logger.registrarInfo("Sala " + sala.getNumero() + " liberada. Sin usuarios en espera.");
                }
                return;
            }
        }
    }

    /** @return Lista de todas las salas de lectura. */
    public ListaEnlazada<SalaLectura> getSalas() { return salas; }

    /** @return Cola de espera general para salas. */
    public Cola<SolicitudSala> getColaSalas() { return colaSalas; }

    // ══════════════════════════════════════════════════════════════
    // MÓDULO DE COMPUTADORAS
    // ══════════════════════════════════════════════════════════════

    /**
     * Registra una computadora en el sistema. O(1).
     *
     * @param computadora Computadora a registrar.
     */
    public void registrarComputadora(Computadora computadora) {
        computadoras.agregar(computadora);
        logger.registrarInfo("Computadora registrada: PC-" + computadora.getNumero());
    }

    /**
     * Solicita una computadora para un usuario.
     * Si hay una libre, la asigna directamente.
     * Si todas estan en uso, encola la solicitud (FIFO usando Cola).
     *
     * @param idUsuario       ID del usuario solicitante.
     * @param nombreUsuario   Nombre del usuario.
     * @param duracionMinutos Minutos de uso solicitados.
     * @return La {@link SolicitudComputadora} creada.
     */
    public SolicitudComputadora solicitarComputadora(String idUsuario, String nombreUsuario, int duracionMinutos) {
        String idSol = "RC" + String.format("%04d", contadorSolicitudesComputadoras++);
        SolicitudComputadora solicitud = new SolicitudComputadora(idSol, idUsuario, nombreUsuario, duracionMinutos);

        for (int i = 0; i < computadoras.getTamanio(); i++) {
            Computadora pc = computadoras.obtener(i);
            if (pc.isDisponible()) {
                pc.asignar(solicitud);
                logger.registrarInfo("PC-" + pc.getNumero() + " asignada a " + nombreUsuario);
                return solicitud;
            }
        }
        colaComputadoras.encolar(solicitud);
        logger.registrarWarning("Todas las computadoras ocupadas. " + nombreUsuario + " en lista de espera.");
        return solicitud;
    }

    /**
     * Libera una computadora por su ID.
     * Si hay solicitudes en cola general, asigna automaticamente la siguiente.
     *
     * @param idComputadora ID de la computadora a liberar.
     */
    public void liberarComputadora(String idComputadora) {
        for (int i = 0; i < computadoras.getTamanio(); i++) {
            Computadora pc = computadoras.obtener(i);
            if (pc.getId().equals(idComputadora)) {
                pc.liberar();
                if (!colaComputadoras.estaVacia()) {
                    SolicitudComputadora siguiente = colaComputadoras.desencolar();
                    pc.asignar(siguiente);
                    logger.registrarInfo("PC-" + pc.getNumero() + " reasignada a " + siguiente.getNombreUsuario());
                } else {
                    logger.registrarInfo("PC-" + pc.getNumero() + " liberada.");
                }
                return;
            }
        }
    }

    /** @return Lista de todas las computadoras. */
    public ListaEnlazada<Computadora> getComputadoras() { return computadoras; }

    /** @return Cola de espera general para computadoras. */
    public Cola<SolicitudComputadora> getColaComputadoras() { return colaComputadoras; }

    // ══════════════════════════════════════════════════════════════
    // GETTERS GENERALES
    // ══════════════════════════════════════════════════════════════

    /** @return El catalogo BST del sistema. */
    public CatalogoBST getCatalogo() { return catalogo; }

    /** @return Lista de todos los usuarios registrados. */
    public ListaEnlazada<Usuario> getUsuarios() { return usuarios; }

    /** @return Lista de todos los prestamos registrados. */
    public ListaEnlazada<Prestamo> getPrestamos() { return prestamos; }
}