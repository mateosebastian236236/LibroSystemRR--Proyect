package librosystemrr.excepciones;

/**
 * Excepción lanzada cuando se busca un libro por ISBN o título y no existe en el catálogo.
 * Se lanza desde {@code CatalogoBST} y {@code SistemaBiblioteca} en lugar de retornar {@code null}.
 */
@SuppressWarnings("serial")
public class LibroNoEncontradoException extends LibroSystemException {

    /**
     * Construye la excepción indicando el ISBN buscado.
     *
     * @param isbn ISBN del libro que no fue encontrado.
     */
    public LibroNoEncontradoException(String isbn) {
        super("No se encontró ningún libro con ISBN: " + isbn);
    }
}