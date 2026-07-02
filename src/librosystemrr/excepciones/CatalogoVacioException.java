package librosystemrr.excepciones;

/**
 * Excepción lanzada cuando se intenta realizar una operación sobre el catálogo
 * (ordenar, listar, buscar) y este se encuentra vacío.
 */
@SuppressWarnings("serial")
public class CatalogoVacioException extends LibroSystemException {

    /**
     * Construye la excepción con un mensaje estándar.
     */
    public CatalogoVacioException() {
        super("El catálogo está vacío. No hay libros registrados en el sistema.");
    }
}