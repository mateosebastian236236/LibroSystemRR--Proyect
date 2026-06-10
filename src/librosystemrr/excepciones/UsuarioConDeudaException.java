package librosystemrr.excepciones;

/**
 * Excepción lanzada cuando un Lector con multas pendientes intenta realizar
 * un nuevo préstamo.
 */
public class UsuarioConDeudaException extends LibroSystemException {

    /**
     * Construye la excepción indicando el ID del usuario moroso.
     *
     * @param id Identificador del usuario que tiene deuda pendiente.
     */
    public UsuarioConDeudaException(String id) {
        super("El usuario con ID " + id + " tiene deudas pendientes y no puede realizar préstamos.");
    }
}