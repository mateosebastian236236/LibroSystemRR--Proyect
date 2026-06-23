package librosystemrr.modelos;

import librosystemrr.tads.Cola;
import librosystemrr.tads.Pila;

/**
 * Clase abstracta que representa a cualquier usuario del sistema de biblioteca.
 * Aplica el patrón Template Method: define la estructura común y delega
 * {@link #getTipo()} a las subclases (Lector, Bibliotecario, AyudanteBibliotecario).
 *
 * <p>Principio OCP: abierta para extensión (nuevas subclases), cerrada para modificación.</p>
 */
public abstract class Usuario {

    /** Identificador único del usuario. */
    private String id;

    /** Nombre completo del usuario. */
    private String nombre;

    /**
     * Cola de préstamos activos (FIFO).
     * El primer préstamo en entrar es el primero en procesarse.
     */
    private Cola<Prestamo> prestamosActivos;

    /**
     * Historial de todos los préstamos realizados (LIFO).
     * El último préstamo registrado aparece primero.
     */
    private Pila<Prestamo> historialPrestamos;

    /**
     * Construye un usuario con ID y nombre.
     *
     * @param id     Identificador único del usuario.
     * @param nombre Nombre completo.
     */
    public Usuario(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.prestamosActivos = new Cola<>();
        this.historialPrestamos = new Pila<>();
    }

    // ══════════════════════════════════════════
    // MÉTODO ABSTRACTO (Template Method)
    // ══════════════════════════════════════════

    /**
     * Retorna el tipo de usuario ("LECTOR", "BIBLIOTECARIO", "AYUDANTE").
     * Cada subclase implementa este método según su rol.
     *
     * @return Cadena que identifica el tipo de usuario.
     */
    public abstract String getTipo();

    // ══════════════════════════════════════════
    // GETTERS
    // ══════════════════════════════════════════

    /** @return Identificador único del usuario. */
    public String getId() { return id; }

    /** @return Nombre completo del usuario. */
    public String getNombre() { return nombre; }

    /** @return Cola de préstamos activos del usuario. */
    public Cola<Prestamo> getPrestamosActivos() { return prestamosActivos; }

    /** @return Pila con el historial de préstamos del usuario. */
    public Pila<Prestamo> getHistorialPrestamos() { return historialPrestamos; }

    @Override
    public String toString() {
        return "Usuario{id='" + id + "', nombre='" + nombre +
                "', tipo=" + getTipo() +
                ", prestamosActivos=" + prestamosActivos.getTamanio() + "}";
    }
}