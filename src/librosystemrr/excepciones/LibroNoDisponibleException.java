package librosystemrr.excepciones;

/**
 * Excepción lanzada cuando se intenta prestar un libro que ya está prestado
 * (disponible = false).
 */
public class LibroNoDisponibleException extends LibroSystemException {

    /**
     * Construye la excepción indicando el ISBN del libro no disponible.
     *
     * @param isbn ISBN del libro que no está disponible.
     */
    public LibroNoDisponibleException(String isbn) {
        super("El libro con ISBN " + isbn + " no está disponible para préstamo.");
    }
}