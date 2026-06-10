package librosystemrr.tads.arbol;

import librosystemrr.modelos.Libro;

/**
 * Nodo del árbol binario de búsqueda (BST) que almacena libros.
 * Cada nodo contiene una referencia a un {@link Libro} y punteros
 * a sus subárboles izquierdo y derecho.
 *
 * <p>El orden del BST se basa en el ISBN del libro (orden lexicográfico).</p>
 */
public class NodoBST {

    /** Libro almacenado en este nodo. */
    private Libro libro;

    /** Subárbol izquierdo (libros con ISBN menor). */
    private NodoBST izquierdo;

    /** Subárbol derecho (libros con ISBN mayor). */
    private NodoBST derecho;

    /**
     * Construye un nodo hoja con el libro especificado.
     *
     * @param libro Libro a almacenar. No debe ser {@code null}.
     */
    public NodoBST(Libro libro) {
        this.libro = libro;
        this.izquierdo = null;
        this.derecho = null;
    }

    /**
     * Retorna el libro almacenado en este nodo.
     *
     * @return El {@link Libro} del nodo.
     */
    public Libro getLibro() {
        return libro;
    }

    /**
     * Retorna el subárbol izquierdo.
     *
     * @return {@link NodoBST} izquierdo, o {@code null} si no existe.
     */
    public NodoBST getIzquierdo() {
        return izquierdo;
    }

    /**
     * Retorna el subárbol derecho.
     *
     * @return {@link NodoBST} derecho, o {@code null} si no existe.
     */
    public NodoBST getDerecho() {
        return derecho;
    }

    /**
     * Establece el subárbol izquierdo.
     *
     * @param izquierdo Nuevo nodo izquierdo.
     */
    public void setIzquierdo(NodoBST izquierdo) {
        this.izquierdo = izquierdo;
    }

    /**
     * Establece el subárbol derecho.
     *
     * @param derecho Nuevo nodo derecho.
     */
    public void setDerecho(NodoBST derecho) {
        this.derecho = derecho;
    }

    @Override
    public String toString() {
        return "NodoBST{" + libro.getIsbn() + "}";
    }
}
