package librosystemrr.tads;

/**
 * Cola genérica (FIFO — First In, First Out). Implementación propia sin usar
 * {@code java.util.Queue} ni derivados.
 *
 * <p>Usada para gestionar los {@code prestamosActivos} de cada {@code Usuario}:
 * el primer préstamo en entrar es el primero en procesarse.</p>
 *
 * @param <T> Tipo de los elementos almacenados.
 */
import java.io.Serializable;

public class Cola<T> implements Serializable {

    /** Nodo al frente de la cola (el siguiente en salir). */
    private Nodo<T> frente;

    /** Nodo al final de la cola (el último en entrar). */
    private Nodo<T> fin;

    /** Cantidad de elementos en la cola. */
    private int tamanio;

    /**
     * Construye una cola vacía.
     */
    public Cola() {
        this.frente = null;
        this.fin = null;
        this.tamanio = 0;
    }

    /**
     * Agrega un elemento al final de la cola. O(1).
     *
     * @param dato Elemento a encolar.
     */
    public void encolar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (estaVacia()) {
            frente = nuevo;
            fin = nuevo;
        } else {
            fin.setSiguiente(nuevo);
            fin = nuevo;
        }
        tamanio++;
    }

    /**
     * Elimina y retorna el elemento al frente de la cola. O(1).
     *
     * @return El elemento que estaba al frente.
     * @throws java.util.NoSuchElementException si la cola está vacía.
     */
    public T desencolar() {
        if (estaVacia()) {
            throw new java.util.NoSuchElementException("No se puede desencolar: la cola está vacía.");
        }
        T dato = frente.getDato();
        frente = frente.getSiguiente();
        if (frente == null) {
            fin = null;
        }
        tamanio--;
        return dato;
    }

    /**
     * Retorna el elemento al frente sin eliminarlo. O(1).
     *
     * @return El elemento al frente de la cola.
     * @throws java.util.NoSuchElementException si la cola está vacía.
     */
    public T getFronte() {
        if (estaVacia()) {
            throw new java.util.NoSuchElementException("La cola está vacía.");
        }
        return frente.getDato();
    }

    /**
     * Retorna la cantidad de elementos en la cola. O(1).
     *
     * @return Número de elementos.
     */
    public int getTamanio() {
        return tamanio;
    }

    /**
     * Indica si la cola no contiene elementos. O(1).
     *
     * @return {@code true} si la cola está vacía.
     */
    public boolean estaVacia() {
        return tamanio == 0;
    }

    @Override
    public String toString() {
        if (estaVacia()) return "Cola[]";
        StringBuilder sb = new StringBuilder("Cola[frente -> ");
        Nodo<T> actual = frente;
        while (actual != null) {
            sb.append(actual.getDato());
            if (actual.getSiguiente() != null) sb.append(", ");
            actual = actual.getSiguiente();
        }
        sb.append(" <- fin]");
        return sb.toString();
    }
}