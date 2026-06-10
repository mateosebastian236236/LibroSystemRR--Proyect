package librosystemrr.tads;

/**
 * Nodo genérico que sirve como bloque base interno de {@link ListaEnlazada},
 * {@link Cola} y {@link Pila}. Implementación propia sin librerías externas.
 *
 * @param <T> Tipo del dato almacenado en el nodo.
 */
public class Nodo<T> {

    /** Dato almacenado en este nodo. */
    private T dato;

    /** Referencia al siguiente nodo en la cadena. */
    private Nodo<T> siguiente;

    /**
     * Construye un nodo con el dato especificado y sin sucesor.
     *
     * @param dato Valor a almacenar en el nodo.
     */
    public Nodo(T dato) {
        this.dato = dato;
        this.siguiente = null;
    }

    /**
     * Retorna el dato almacenado en este nodo.
     *
     * @return El dato de tipo {@code T}.
     */
    public T getDato() {
        return dato;
    }

    /**
     * Establece el dato del nodo.
     *
     * @param dato Nuevo dato a almacenar.
     */
    public void setDato(T dato) {
        this.dato = dato;
    }

    /**
     * Retorna el nodo siguiente en la cadena.
     *
     * @return Referencia al {@link Nodo} siguiente, o {@code null} si es el último.
     */
    public Nodo<T> getSiguiente() {
        return siguiente;
    }

    /**
     * Establece el nodo siguiente en la cadena.
     *
     * @param siguiente Nodo que seguirá a este en la estructura.
     */
    public void setSiguiente(Nodo<T> siguiente) {
        this.siguiente = siguiente;
    }

    @Override
    public String toString() {
        return "Nodo{" + dato + "}";
    }
}