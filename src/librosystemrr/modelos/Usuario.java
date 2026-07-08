package librosystemrr.modelos;

import librosystemrr.tads.Cola;
import librosystemrr.tads.Pila;
import java.io.Serializable;

/**
 * Clase abstracta que representa a cualquier usuario del sistema de biblioteca.
 * Aplica el patrón Template Method: define la estructura común y delega
 * {@link #getTipo()} a las subclases.
 */
public abstract class Usuario implements Serializable {

    /** Identificador único del usuario. */
    private String id;

    /** Nombre completo del usuario. */
    private String nombre;

    /** Contraseña de acceso al sistema. */
    private String contrasena;

    /** Contraseña para el inicio de sesión. */
    private String contrasena;

    /** Cola de préstamos activos (FIFO). */
    private Cola<Prestamo> prestamosActivos;

    /** Historial de todos los préstamos realizados (LIFO). */
    private Pila<Prestamo> historialPrestamos;

    /**
     * Construye un usuario con ID, nombre y contraseña personalizada.
     *
     * @param id         Identificador único.
     * @param nombre     Nombre completo.
     * @param contrasena Contraseña de acceso al sistema.
     */
    public Usuario(String id, String nombre, String contrasena) {
        this.id = id;
        this.nombre = nombre;
        this.contrasena = contrasena;
        this.prestamosActivos = new Cola<>();
        this.historialPrestamos = new Pila<>();
    }

    /**
     * Construye un usuario con contraseña por defecto "1234".
     *
     * @param id     Identificador único.
     * @param nombre Nombre completo.
     */
    public Usuario(String id, String nombre) {
        this(id, nombre, "1234");
    }

    // ══════════════════════════════════════════
    // MÉTODO ABSTRACTO (Template Method)
    // ══════════════════════════════════════════

    /**
     * Retorna el tipo de usuario ("LECTOR", "BIBLIOTECARIO", "AYUDANTE").
     *
     * @return Cadena que identifica el tipo de usuario.
     */
    public abstract String getTipo();

    // ══════════════════════════════════════════
    // GETTERS
    // ══════════════════════════════════════════

    /** @return Contraseña del usuario (uso interno de autenticación). */
    public String getContrasena() { return contrasena; }

    /** @return Identificador único del usuario. */
    public String getId() { return id; }

    /** @return Nombre completo del usuario. */
    public String getNombre() { return nombre; }

    /** @return Contraseña del usuario. */
    public String getContrasena() { return contrasena; }

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