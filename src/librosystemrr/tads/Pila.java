package librosystemrr.tads;

/**
 * Pila genérica (LIFO — Last In, First Out). Implementación propia sin usar
 * {@code java.util.Stack} ni derivados.
 *
 * <p>Usada para gestionar el {@code historialPrestamos} de cada {@code Usuario}:
 * el último préstamo registrado es el primero en consultarse.</p>
 *
 * @param <T> Tipo de los elementos almacenados.
 */
public class Pila<T> {

    /** Nodo en la cima de la pila. */
    private Nodo<T> tope;

    /** Cantidad de elementos en la pila. */
    private int tamanio;

    /**
     * Construye una pila vacía.
     */
    public Pila() {
        this.tope = null;
        this.tamanio = 0;
    }

    /**
     * Apila un elemento en la cima. O(1).
     *
     * @param dato Elemento a apilar.
     */
    public void apilar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.setSiguiente(tope);
        tope = nuevo;
        tamanio++;
    }

    /**
     * Elimina y retorna el elemento en la cima de la pila. O(1).
     *
     * @return El elemento que estaba en la cima.
     * @throws java.util.NoSuchElementException si la pila está vacía.
     */
    public T desapilar() {
        if (estaVacia()) {
            throw new java.util.NoSuchElementException("No se puede desapilar: la pila está vacía.");
        }
        T dato = tope.getDato();
        tope = tope.getSiguiente();
        tamanio--;
        return dato;
    }

    /**
     * Retorna el elemento en la cima sin eliminarlo. O(1).
     *
     * @return El elemento en la cima.
     * @throws java.util.NoSuchElementException si la pila está vacía.
     */
    public T getTope() {
        if (estaVacia()) {
            throw new java.util.NoSuchElementException("La pila está vacía.");
        }
        return tope.getDato();
    }

    /**
     * Retorna la cantidad de elementos en la pila. O(1).
     *
     * @return Número de elementos.
     */
    public int getTamanio() {
        return tamanio;
    }

    /**
     * Indica si la pila no contiene elementos. O(1).
     *
     * @return {@code true} si la pila está vacía.
     */
    public boolean estaVacia() {
        return tamanio == 0;
    }

    @Override
    public String toString() {
        if (estaVacia()) return "Pila[]";
        StringBuilder sb = new StringBuilder("Pila[tope -> ");
        Nodo<T> actual = tope;
        while (actual != null) {
            sb.append(actual.getDato());
            if (actual.getSiguiente() != null) sb.append(", ");
            actual = actual.getSiguiente();
        }
        sb.append("]");
        return sb.toString();
    }
}
