package librosystemrr.modelos;

import librosystemrr.tads.Cola;
import librosystemrr.tads.Pila;
import java.io.Serializable;

/**
 * Clase abstracta que representa a cualquier usuario del sistema de biblioteca.
 * La cédula (10 dígitos) es el identificador único de cada usuario.
 */
public abstract class Usuario implements Serializable {

    private static final long serialVersionUID = 2L;

    /** Cédula de identidad (10 dígitos, única por usuario). */
    private String cedula;

    /** Nombre completo del usuario. */
    private String nombre;

    /** Contraseña de acceso al sistema. */
    private String contrasena;

    /** Cola de préstamos activos (FIFO). */
    private Cola<Prestamo> prestamosActivos;

    /** Historial de todos los préstamos realizados (LIFO). */
    private Pila<Prestamo> historialPrestamos;

    /**
     * Construye un usuario con cédula, nombre y contraseña.
     *
     * @param cedula     Cédula de 10 dígitos. Debe ser válida y única.
     * @param nombre     Nombre completo.
     * @param contrasena Contraseña de acceso.
     */
    public Usuario(String cedula, String nombre, String contrasena) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.contrasena = contrasena;
        this.prestamosActivos = new Cola<>();
        this.historialPrestamos = new Pila<>();
    }

    // ══════════════════════════════════════════
    // VALIDACIÓN
    // ══════════════════════════════════════════

    /**
     * Valida que una cédula tenga exactamente 10 dígitos numéricos.
     *
     * @param cedula Cadena a validar.
     * @return {@code true} si es una cédula válida.
     */
    public static boolean esCedulaValida(String cedula) {
        return cedula != null && cedula.matches("\\d{10}");
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

    /** @return Cédula única del usuario. */
    public String getCedula() { return cedula; }

    /**
     * Alias de {@link #getCedula()} para compatibilidad interna.
     *
     * @return Cédula del usuario.
     */
    public String getId() { return cedula; }

    /** @return Nombre completo del usuario. */
    public String getNombre() { return nombre; }

    /** @return Contraseña del usuario (uso interno de autenticación). */
    public String getContrasena() { return contrasena; }

    /** @return Cola de préstamos activos del usuario. */
    public Cola<Prestamo> getPrestamosActivos() { return prestamosActivos; }

    /** @return Pila con el historial de préstamos del usuario. */
    public Pila<Prestamo> getHistorialPrestamos() { return historialPrestamos; }

    @Override
    public String toString() {
        return "Usuario{cedula='" + cedula + "', nombre='" + nombre +
                "', tipo=" + getTipo() + "}";
    }
}