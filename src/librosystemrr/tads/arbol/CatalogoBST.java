package librosystemrr.tads.arbol;

import librosystemrr.excepciones.CatalogoVacioException;
import librosystemrr.excepciones.LibroNoEncontradoException;
import librosystemrr.interfaces.Buscable;
import librosystemrr.modelos.Libro;
import librosystemrr.tads.ListaEnlazada;

/**
 * Árbol Binario de Búsqueda (BST) que gestiona el catálogo de libros.
 * Implementa {@link Buscable} para proveer búsqueda por ISBN y por título.
 *
 * <p>El criterio de orden es el ISBN del libro (comparación lexicográfica).</p>
 *
 * <ul>
 *   <li>Insertar — O(log n) promedio / O(n) peor caso (árbol degenerado)</li>
 *   <li>Buscar por ISBN — O(log n) promedio / O(n) peor caso</li>
 *   <li>Buscar por título — O(n) siempre (recorre inorden)</li>
 *   <li>Inorden — O(n)</li>
 *   <li>Eliminar — O(log n) promedio / O(n) peor caso</li>
 * </ul>
 */
public class CatalogoBST implements Buscable {

    /** Raíz del árbol. */
    private NodoBST raiz;

    /** Contador de libros insertados en el árbol. */
    private int tamanio;

    /**
     * Construye un catálogo vacío.
     */
    public CatalogoBST() {
        this.raiz = null;
        this.tamanio = 0;
    }

    // ══════════════════════════════════════════
    // MÉTODOS PÚBLICOS
    // ══════════════════════════════════════════

    /**
     * Inserta un libro en el árbol manteniendo la propiedad BST (por ISBN). O(log n) prom.
     *
     * @param libro Libro a insertar. No debe ser {@code null}.
     */
    public void insertar(Libro libro) {
        raiz = insertarRec(raiz, libro);
        tamanio++;
    }

    /**
     * Busca un libro por su ISBN. O(log n) prom / O(n) peor.
     *
     * @param isbn ISBN exacto del libro buscado.
     * @return El {@link Libro} encontrado.
     * @throws LibroNoEncontradoException si el ISBN no existe en el catálogo.
     */
    @Override
    public Libro buscarPorIsbn(String isbn) {
        Libro encontrado = buscarRec(raiz, isbn);
        if (encontrado == null) {
            throw new LibroNoEncontradoException(isbn);
        }
        return encontrado;
    }

    /**
     * Busca libros cuyo título contenga la cadena indicada (insensible a mayúsculas). O(n).
     *
     * @param titulo Fragmento del título a buscar.
     * @return {@link ListaEnlazada} con los libros que coinciden (puede estar vacía).
     * @throws CatalogoVacioException si el catálogo está vacío.
     */
    @Override
    public ListaEnlazada<Libro> buscarPorTitulo(String titulo) {
        if (estaVacio()) {
            throw new CatalogoVacioException();
        }
        ListaEnlazada<Libro> resultados = new ListaEnlazada<>();
        buscarPorTituloRec(raiz, titulo.toLowerCase(), resultados);
        return resultados;
    }

    /**
     * Elimina el libro con el ISBN especificado del árbol. O(log n) prom.
     *
     * @param isbn ISBN del libro a eliminar.
     * @return {@code true} si fue eliminado, {@code false} si no se encontró.
     */
    public boolean eliminar(String isbn) {
        int tamanioAntes = tamanio;
        raiz = eliminarRec(raiz, isbn);
        return tamanio < tamanioAntes;
    }

    /**
     * Retorna todos los libros del catálogo en orden inorden (ascendente por ISBN). O(n).
     *
     * @return {@link ListaEnlazada} con todos los libros ordenados por ISBN.
     * @throws CatalogoVacioException si el catálogo está vacío.
     */
    public ListaEnlazada<Libro> inorden() {
        if (estaVacio()) {
            throw new CatalogoVacioException();
        }
        ListaEnlazada<Libro> lista = new ListaEnlazada<>();
        inordenRec(raiz, lista);
        return lista;
    }

    /**
     * Indica si el catálogo no contiene libros. O(1).
     *
     * @return {@code true} si el árbol está vacío.
     */
    public boolean estaVacio() {
        return tamanio == 0;
    }

    /**
     * Retorna la cantidad de libros en el catálogo. O(1).
     *
     * @return Número de libros registrados.
     */
    public int getTamanio() {
        return tamanio;
    }

    // ══════════════════════════════════════════
    // MÉTODOS PRIVADOS «recursive»
    // ══════════════════════════════════════════

    /**
     * Inserta recursivamente un libro en el subárbol con raíz {@code nodo}.
     *
     * @param nodo  Raíz del subárbol actual.
     * @param libro Libro a insertar.
     * @return La nueva raíz del subárbol tras la inserción.
     */
    private NodoBST insertarRec(NodoBST nodo, Libro libro) {
        if (nodo == null) {
            return new NodoBST(libro);
        }
        int cmp = libro.getIsbn().compareTo(nodo.getLibro().getIsbn());
        if (cmp < 0) {
            nodo.setIzquierdo(insertarRec(nodo.getIzquierdo(), libro));
        } else if (cmp > 0) {
            nodo.setDerecho(insertarRec(nodo.getDerecho(), libro));
        }
        // ISBN duplicado: no se inserta (política: ISBN es clave única)
        return nodo;
    }

    /**
     * Busca recursivamente un libro por ISBN en el subárbol con raíz {@code nodo}.
     *
     * @param nodo Raíz del subárbol actual.
     * @param isbn ISBN buscado.
     * @return El {@link Libro} encontrado, o {@code null} si no existe.
     */
    private Libro buscarRec(NodoBST nodo, String isbn) {
        if (nodo == null) return null;
        int cmp = isbn.compareTo(nodo.getLibro().getIsbn());
        if (cmp == 0) return nodo.getLibro();
        if (cmp < 0) return buscarRec(nodo.getIzquierdo(), isbn);
        return buscarRec(nodo.getDerecho(), isbn);
    }

    /**
     * Recorre recursivamente el árbol en inorden y agrega cada libro a {@code lista}.
     *
     * @param nodo  Raíz del subárbol actual.
     * @param lista Lista donde se acumulan los libros en orden.
     */
    private void inordenRec(NodoBST nodo, ListaEnlazada<Libro> lista) {
        if (nodo == null) return;
        inordenRec(nodo.getIzquierdo(), lista);
        lista.agregar(nodo.getLibro());
        inordenRec(nodo.getDerecho(), lista);
    }

    /**
     * Busca recursivamente en inorden los libros cuyo título contenga {@code tituloBuscar}.
     *
     * @param nodo         Raíz del subárbol actual.
     * @param tituloBuscar Fragmento del título en minúsculas.
     * @param resultados   Lista donde se acumulan los libros que coinciden.
     */
    private void buscarPorTituloRec(NodoBST nodo, String tituloBuscar, ListaEnlazada<Libro> resultados) {
        if (nodo == null) return;
        buscarPorTituloRec(nodo.getIzquierdo(), tituloBuscar, resultados);
        if (nodo.getLibro().getTitulo().toLowerCase().contains(tituloBuscar)) {
            resultados.agregar(nodo.getLibro());
        }
        buscarPorTituloRec(nodo.getDerecho(), tituloBuscar, resultados);
    }

    /**
     * Elimina recursivamente el nodo con el ISBN dado del subárbol con raíz {@code nodo}.
     * Usa el sucesor inorden (mínimo del subárbol derecho) para reemplazar el nodo eliminado.
     *
     * @param nodo Raíz del subárbol actual.
     * @param isbn ISBN del libro a eliminar.
     * @return La nueva raíz del subárbol tras la eliminación.
     */
    private NodoBST eliminarRec(NodoBST nodo, String isbn) {
        if (nodo == null) return null;

        int cmp = isbn.compareTo(nodo.getLibro().getIsbn());
        if (cmp < 0) {
            nodo.setIzquierdo(eliminarRec(nodo.getIzquierdo(), isbn));
        } else if (cmp > 0) {
            nodo.setDerecho(eliminarRec(nodo.getDerecho(), isbn));
        } else {
            // Nodo encontrado — tres casos
            tamanio--;
            if (nodo.getIzquierdo() == null) return nodo.getDerecho();
            if (nodo.getDerecho() == null) return nodo.getIzquierdo();
            // Nodo con dos hijos: reemplazar con sucesor inorden
            NodoBST sucesor = minimoNodo(nodo.getDerecho());
            nodo = new NodoBST(sucesor.getLibro());
            nodo.setDerecho(eliminarRec(nodo.getDerecho(), sucesor.getLibro().getIsbn()));
            tamanio++; // se decrementó en la llamada recursiva; compensar
        }
        return nodo;
    }

    /**
     * Retorna el nodo con el ISBN mínimo en el subárbol dado (el más a la izquierda). O(h).
     *
     * @param nodo Raíz del subárbol donde buscar el mínimo.
     * @return El {@link NodoBST} con el ISBN mínimo.
     */
    private NodoBST minimoNodo(NodoBST nodo) {
        while (nodo.getIzquierdo() != null) {
            nodo = nodo.getIzquierdo();
        }
        return nodo;
    }
}