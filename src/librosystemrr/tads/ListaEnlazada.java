package librosystemrr.tads;

/**
 * Lista enlazada simple genérica. Implementación propia sin usar
 * {@code java.util.ArrayList} ni {@code java.util.LinkedList}.
 *
 * <p>Utilizada como estructura principal para almacenar usuarios,
 * préstamos activos y resultados de búsqueda en el sistema.</p>
 *
 * @param <T> Tipo de los elementos almacenados.
 */
import java.io.Serializable;

public class ListaEnlazada<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Primer nodo de la lista. */
    private Nodo<T> cabeza;

    /** Cantidad de elementos en la lista. */
    private int tamanio;

    /**
     * Construye una lista vacía.
     */
    public ListaEnlazada() {
        this.cabeza = null;
        this.tamanio = 0;
    }

    /**
     * Agrega un elemento al final de la lista. O(n).
     *
     * @param dato Elemento a agregar.
     */
    public void agregar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            Nodo<T> actual = cabeza;
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nuevo);
        }
        tamanio++;
    }

    /**
     * Agrega un elemento al inicio de la lista. O(1).
     *
     * @param dato Elemento a agregar al principio.
     */
    public void agregarAlInicio(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.setSiguiente(cabeza);
        cabeza = nuevo;
        tamanio++;
    }

    /**
     * Elimina la primera ocurrencia del elemento especificado. O(n).
     *
     * @param dato Elemento a eliminar (comparado por {@code equals}).
     * @return {@code true} si fue eliminado, {@code false} si no se encontró.
     */
    public boolean eliminar(T dato) {
        if (cabeza == null) return false;

        if (cabeza.getDato().equals(dato)) {
            cabeza = cabeza.getSiguiente();
            tamanio--;
            return true;
        }

        Nodo<T> actual = cabeza;
        while (actual.getSiguiente() != null) {
            if (actual.getSiguiente().getDato().equals(dato)) {
                actual.setSiguiente(actual.getSiguiente().getSiguiente());
                tamanio--;
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    /**
     * Retorna el elemento en la posición indicada. O(n).
     *
     * @param indice Índice base 0 del elemento a obtener.
     * @return El elemento en la posición {@code indice}.
     * @throws IndexOutOfBoundsException si el índice está fuera de rango.
     */
    public T obtener(int indice) {
        if (indice < 0 || indice >= tamanio) {
            throw new IndexOutOfBoundsException("Índice " + indice + " fuera de rango. Tamaño: " + tamanio);
        }
        Nodo<T> actual = cabeza;
        for (int i = 0; i < indice; i++) {
            actual = actual.getSiguiente();
        }
        return actual.getDato();
    }

    /**
     * Busca la primera ocurrencia del elemento por igualdad ({@code equals}). O(n).
     *
     * @param dato Elemento a buscar.
     * @return El elemento encontrado, o {@code null} si no existe.
     */
    public T buscar(T dato) {
        Nodo<T> actual = cabeza;
        while (actual != null) {
            if (actual.getDato().equals(dato)) {
                return actual.getDato();
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    /**
     * Retorna la cantidad de elementos en la lista. O(1).
     *
     * @return Número de elementos.
     */
    public int getTamanio() {
        return tamanio;
    }

    /**
     * Indica si la lista no contiene elementos. O(1).
     *
     * @return {@code true} si la lista está vacía.
     */
    public boolean estaVacia() {
        return tamanio == 0;
    }

    /**
     * Convierte la lista en un arreglo de tipo {@code Object[]}. O(n).
     * Nota: Java no permite crear arreglos genéricos directamente;
     * el llamador debe hacer el cast correspondiente.
     *
     * @return Arreglo con todos los elementos de la lista en orden.
     */
    public Object[] toArray() {
        Object[] arreglo = new Object[tamanio];
        Nodo<T> actual = cabeza;
        for (int i = 0; i < tamanio; i++) {
            arreglo[i] = actual.getDato();
            actual = actual.getSiguiente();
        }
        return arreglo;
    }

    /**
     * Retorna el nodo cabeza (uso interno para algoritmos de Semana 4).
     *
     * @return Nodo cabeza de la lista.
     */
    public Nodo<T> getCabeza() {
        return cabeza;
    }

    /**
     * Establece el nodo cabeza (uso interno para MergeSort en Semana 4).
     *
     * @param cabeza Nuevo nodo cabeza.
     */
    public void setCabeza(Nodo<T> cabeza) {
        this.cabeza = cabeza;
    }

    /**
     * Establece el tamaño de la lista (uso interno para MergeSort en Semana 4).
     *
     * @param tamanio Nuevo tamaño.
     */
    public void setTamanio(int tamanio) {
        this.tamanio = tamanio;
    }

    @Override
    public String toString() {
        if (estaVacia()) return "ListaEnlazada[]";
        StringBuilder sb = new StringBuilder("ListaEnlazada[");
        Nodo<T> actual = cabeza;
        while (actual != null) {
            sb.append(actual.getDato());
            if (actual.getSiguiente() != null) sb.append(" -> ");
            actual = actual.getSiguiente();
        }
        sb.append("]");
        return sb.toString();
    }
}