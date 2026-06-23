package librosystemrr.algoritmos;

import librosystemrr.modelos.Libro;
import librosystemrr.modelos.Prestamo;
import librosystemrr.tads.ListaEnlazada;
import librosystemrr.tads.Nodo;

/**
 * Clase utilitaria que implementa los algoritmos de ordenamiento del sistema.
 * Todos los métodos son estáticos. No usa java.util.Arrays ni java.util.Collections.
 *
 * <p>Algoritmos implementados:</p>
 * <ul>
 *   <li>MergeSort sobre {@link ListaEnlazada} — O(n log n) siempre, estable.</li>
 *   <li>QuickSort in-place sobre {@code Libro[]} — O(n log n) promedio, O(n²) peor caso.</li>
 * </ul>
 */
public class AlgoritmosOrdenamiento {

    // ══════════════════════════════════════════════════════════════
    // ORDENAR POR TÍTULO — MergeSort sobre ListaEnlazada<Libro>
    // Complejidad: O(n log n) promedio y peor caso | Estable: Sí
    // ══════════════════════════════════════════════════════════════

    /**
     * Ordena la lista de libros por título (orden alfabético ascendente) usando MergeSort.
     * Modifica la lista original directamente.
     *
     * <p>Complejidad: O(n log n) — MergeSort sobre lista enlazada.</p>
     *
     * @param lista {@link ListaEnlazada} de libros a ordenar.
     */
    public static void ordenarPorTitulo(ListaEnlazada<Libro> lista) {
        if (lista == null || lista.getTamanio() <= 1) return;
        Nodo<Libro> nuevaCabeza = mergeSortTitulo(lista.getCabeza());
        lista.setCabeza(nuevaCabeza);
    }

    /**
     * Ordena recursivamente (MergeSort) los nodos por título.
     *
     * @param cabeza Cabeza del subconjunto de nodos a ordenar.
     * @return Cabeza del subconjunto ya ordenado.
     */
    private static Nodo<Libro> mergeSortTitulo(Nodo<Libro> cabeza) {
        // Caso base: lista vacía o un solo nodo
        if (cabeza == null || cabeza.getSiguiente() == null) return cabeza;

        // Dividir en dos mitades con el algoritmo tortuga-liebre
        Nodo<Libro> mitad = dividirMitad(cabeza);
        Nodo<Libro> segundaMitad = mitad.getSiguiente();
        mitad.setSiguiente(null); // cortar la lista en dos

        // Ordenar recursivamente cada mitad
        Nodo<Libro> izquierda = mergeSortTitulo(cabeza);
        Nodo<Libro> derecha = mergeSortTitulo(segundaMitad);

        // Fusionar las dos mitades ordenadas
        return fusionarTitulo(izquierda, derecha);
    }

    /**
     * Fusiona dos sublistas de nodos ordenadas por título en una sola lista ordenada.
     *
     * @param a Primera sublista ordenada.
     * @param b Segunda sublista ordenada.
     * @return Cabeza de la lista fusionada y ordenada.
     */
    private static Nodo<Libro> fusionarTitulo(Nodo<Libro> a, Nodo<Libro> b) {
        if (a == null) return b;
        if (b == null) return a;

        if (a.getDato().compareTo(b.getDato()) <= 0) {
            a.setSiguiente(fusionarTitulo(a.getSiguiente(), b));
            return a;
        } else {
            b.setSiguiente(fusionarTitulo(a, b.getSiguiente()));
            return b;
        }
    }

    // ══════════════════════════════════════════════════════════════
    // ORDENAR POR AUTOR — MergeSort sobre ListaEnlazada<Libro>
    // Complejidad: O(n log n) promedio y peor caso | Estable: Sí
    // ══════════════════════════════════════════════════════════════

    /**
     * Ordena la lista de libros por autor (orden alfabético ascendente) usando MergeSort.
     * Modifica la lista original directamente.
     *
     * <p>Complejidad: O(n log n) — MergeSort sobre lista enlazada.</p>
     *
     * @param lista {@link ListaEnlazada} de libros a ordenar.
     */
    public static void ordenarPorAutor(ListaEnlazada<Libro> lista) {
        if (lista == null || lista.getTamanio() <= 1) return;
        Nodo<Libro> nuevaCabeza = mergeSortAutor(lista.getCabeza());
        lista.setCabeza(nuevaCabeza);
    }

    /**
     * Ordena recursivamente (MergeSort) los nodos por autor.
     *
     * @param cabeza Cabeza del subconjunto de nodos a ordenar.
     * @return Cabeza del subconjunto ya ordenado.
     */
    private static Nodo<Libro> mergeSortAutor(Nodo<Libro> cabeza) {
        if (cabeza == null || cabeza.getSiguiente() == null) return cabeza;

        Nodo<Libro> mitad = dividirMitad(cabeza);
        Nodo<Libro> segundaMitad = mitad.getSiguiente();
        mitad.setSiguiente(null);

        Nodo<Libro> izquierda = mergeSortAutor(cabeza);
        Nodo<Libro> derecha = mergeSortAutor(segundaMitad);

        return fusionarAutor(izquierda, derecha);
    }

    /**
     * Fusiona dos sublistas ordenadas por autor.
     *
     * @param a Primera sublista ordenada.
     * @param b Segunda sublista ordenada.
     * @return Cabeza de la lista fusionada y ordenada.
     */
    private static Nodo<Libro> fusionarAutor(Nodo<Libro> a, Nodo<Libro> b) {
        if (a == null) return b;
        if (b == null) return a;

        if (a.getDato().compareToAutor(b.getDato()) <= 0) {
            a.setSiguiente(fusionarAutor(a.getSiguiente(), b));
            return a;
        } else {
            b.setSiguiente(fusionarAutor(a, b.getSiguiente()));
            return b;
        }
    }

    // ══════════════════════════════════════════════════════════════
    // ORDENAR POR FECHA DE DEVOLUCIÓN — QuickSort sobre Prestamo[]
    // Complejidad: O(n log n) promedio / O(n²) peor | In-place
    // ══════════════════════════════════════════════════════════════

    /**
     * Ordena un arreglo de préstamos por fecha de devolución (ascendente) usando QuickSort in-place.
     *
     * <p>Complejidad: O(n log n) promedio / O(n²) peor caso (arreglo ya ordenado).</p>
     *
     * @param prestamos Arreglo de préstamos a ordenar.
     */
    public static void ordenarPorFechaDevolucion(Prestamo[] prestamos) {
        if (prestamos == null || prestamos.length <= 1) return;
        quickSortRec(prestamos, 0, prestamos.length - 1);
    }

    /**
     * Aplica QuickSort recursivamente sobre el subarreglo [inicio, fin].
     *
     * @param prestamos Arreglo completo de préstamos.
     * @param inicio    Índice inicial del subarreglo.
     * @param fin       Índice final del subarreglo.
     */
    private static void quickSortRec(Prestamo[] prestamos, int inicio, int fin) {
        if (inicio < fin) {
            int indicePivote = partition(prestamos, inicio, fin);
            quickSortRec(prestamos, inicio, indicePivote - 1);
            quickSortRec(prestamos, indicePivote + 1, fin);
        }
    }

    /**
     * Partición in-place de QuickSort. Elige el último elemento como pivote
     * y coloca los elementos menores a su izquierda y los mayores a su derecha.
     *
     * @param prestamos Arreglo de préstamos.
     * @param inicio    Índice inicial del subarreglo.
     * @param fin       Índice final (pivote).
     * @return Índice final del pivote tras la partición.
     */
    private static int partition(Prestamo[] prestamos, int inicio, int fin) {
        Prestamo pivote = prestamos[fin];
        int i = inicio - 1;

        for (int j = inicio; j < fin; j++) {
            // Comparar fecha de devolución con la del pivote
            if (prestamos[j].getFechaDevolucion().before(pivote.getFechaDevolucion())
                    || prestamos[j].getFechaDevolucion().equals(pivote.getFechaDevolucion())) {
                i++;
                // Intercambiar prestamos[i] y prestamos[j]
                Prestamo temp = prestamos[i];
                prestamos[i] = prestamos[j];
                prestamos[j] = temp;
            }
        }
        // Colocar pivote en su posición correcta
        Prestamo temp = prestamos[i + 1];
        prestamos[i + 1] = prestamos[fin];
        prestamos[fin] = temp;

        return i + 1;
    }

    // ══════════════════════════════════════════════════════════════
    // UTILIDAD INTERNA — Algoritmo tortuga-liebre para dividir lista
    // ══════════════════════════════════════════════════════════════

    /**
     * Encuentra el nodo del punto medio de la lista usando el algoritmo tortuga-liebre.
     * La liebre avanza de dos en dos; la tortuga, de uno en uno.
     * Cuando la liebre llega al final, la tortuga está en el medio.
     *
     * @param cabeza Cabeza de la lista a dividir.
     * @return El nodo que corresponde a la primera mitad (el de antes del punto medio).
     */
    private static <T> Nodo<T> dividirMitad(Nodo<T> cabeza) {
        Nodo<T> tortuga = cabeza;
        Nodo<T> liebre = cabeza.getSiguiente();

        while (liebre != null && liebre.getSiguiente() != null) {
            tortuga = tortuga.getSiguiente();
            liebre = liebre.getSiguiente().getSiguiente();
        }
        return tortuga;
    }
}