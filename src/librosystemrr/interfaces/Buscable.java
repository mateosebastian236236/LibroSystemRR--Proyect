package librosystemrr.interfaces;

import librosystemrr.modelos.Libro;
import librosystemrr.tads.ListaEnlazada;

/**
 * Interfaz que define las operaciones de búsqueda sobre el catálogo.
 * Principio ISP: separada de Ordenable para que ninguna clase implemente
 * métodos que no necesita.
 */
public interface Buscable {

    /**
     * Busca un libro por su ISBN.
     *
     * @param isbn ISBN del libro a buscar.
     * @return El {@link Libro} encontrado.
     * @throws librosystemrr.excepciones.LibroNoEncontradoException si no existe.
     */
    Libro buscarPorIsbn(String isbn);

    /**
     * Busca libros cuyo título contenga la cadena indicada (búsqueda parcial).
     *
     * @param titulo Cadena a buscar en el título.
     * @return {@link ListaEnlazada} con los libros que coincidan.
     */
    ListaEnlazada<Libro> buscarPorTitulo(String titulo);
}
