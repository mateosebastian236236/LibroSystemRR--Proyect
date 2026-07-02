package librosystemrr.excepciones;

/**
 * Excepción lanzada cuando se busca un usuario por ID y no existe en el sistema.
 */
@SuppressWarnings("serial")
public class UsuarioNoEncontradoException extends LibroSystemException {

    /**
     * Construye la excepción indicando el ID buscado.
     *
     * @param id Identificador del usuario que no fue encontrado.
     */
    public UsuarioNoEncontradoException(String id) {
        super("No se encontró ningún usuario con ID: " + id);
    }
}