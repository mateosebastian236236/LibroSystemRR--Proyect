package librosystemrr.algoritmos;

import librosystemrr.excepciones.UsuarioNoEncontradoException;
import librosystemrr.modelos.Libro;
import librosystemrr.modelos.Prestamo;
import librosystemrr.modelos.Usuario;
import librosystemrr.tads.ListaEnlazada;
import librosystemrr.tads.arbol.NodoBST;

/**
 * Clase utilitaria que implementa los algoritmos de búsqueda del sistema.
 * Todos los métodos son estáticos. No usa java.util ni librerías externas.
 *
 * <p>Algoritmos implementados:</p>
 * <ul>
 *   <li>Búsqueda lineal sobre {@link ListaEnlazada} — O(n).</li>
 *   <li>Búsqueda binaria sobre {@code Libro[]} ordenado — O(log n).</li>
 *   <li>Búsqueda en BST recursiva — O(log n) prom / O(n) peor.</li>
 *   <li>Búsqueda de préstamos vencidos — O(n).</li>
 * </ul>
 */
public class AlgoritmosBusqueda {

    // ══════════════════════════════════════════════════════════════
    // BÚSQUEDA LINEAL — Sebastián (Lunes Semana 4)
    // ══════════════════════════════════════════════════════════════

    /**
     * Busca un libro por ISBN exacto en una lista enlazada recorriéndola secuencialmente. O(n).
     * A diferencia de la búsqueda en BST, no requiere que la lista esté ordenada.
     *
     * <p>Complejidad: O(n) — recorre la lista hasta encontrar el ISBN o llegar al final.</p>
     *
     * @param lista Lista de libros donde buscar.
     * @param isbn  ISBN exacto del libro buscado.
     * @return El {@link Libro} encontrado, o {@code null} si no existe.
     */
    public static Libro busquedaLineal(ListaEnlazada<Libro> lista, String isbn) {
        for (int i = 0; i < lista.getTamanio(); i++) {
            Libro libro = lista.obtener(i);
            if (libro.getIsbn().equals(isbn)) {
                return libro;
            }
        }
        return null;
    }

    /**
     * Busca un libro por título (coincidencia parcial) en la lista enlazada. O(n).
     * Recorre todos los nodos comparando por título (insensible a mayúsculas).
     *
     * <p>Complejidad: O(n) — siempre recorre toda la lista en el peor caso.</p>
     *
     * @param lista  Lista de libros donde buscar.
     * @param titulo Fragmento del título a buscar.
     * @return {@link ListaEnlazada} con los libros que coincidan (puede estar vacía).
     */
    public static ListaEnlazada<Libro> buscarPorTituloLineal(ListaEnlazada<Libro> lista, String titulo) {
        ListaEnlazada<Libro> resultados = new ListaEnlazada<>();
        String tituloBuscar = titulo.toLowerCase();
        for (int i = 0; i < lista.getTamanio(); i++) {
            Libro libro = lista.obtener(i);
            if (libro.getTitulo().toLowerCase().contains(tituloBuscar)) {
                resultados.agregar(libro);
            }
        }
        return resultados;
    }

    /**
     * Busca todos los préstamos cuya fecha de devolución ya venció. O(n).
     *
     * <p>Complejidad: O(n) — recorre toda la lista de préstamos.</p>
     *
     * @param prestamos Lista de todos los préstamos del sistema.
     * @return {@link ListaEnlazada} con los préstamos vencidos.
     */
    public static ListaEnlazada<Prestamo> buscarVencidos(ListaEnlazada<Prestamo> prestamos) {
        ListaEnlazada<Prestamo> vencidos = new ListaEnlazada<>();
        for (int i = 0; i < prestamos.getTamanio(); i++) {
            Prestamo p = prestamos.obtener(i);
            if (p.isVencido()) {
                vencidos.agregar(p);
            }
        }
        return vencidos;
    }

    // ══════════════════════════════════════════════════════════════
    // BÚSQUEDA BINARIA — Sebastián (Lunes Semana 4)
    // ══════════════════════════════════════════════════════════════

    /**
     * Busca un libro por ISBN en un arreglo de libros ya ordenado por ISBN. O(log n).
     * El arreglo DEBE estar ordenado por ISBN para que la búsqueda sea correcta.
     *
     * <p>Complejidad: O(log n) — descarta la mitad del arreglo en cada iteración.</p>
     *
     * @param libros Arreglo de libros ordenado por ISBN.
     * @param isbn   ISBN exacto a buscar.
     * @return El {@link Libro} encontrado, o {@code null} si no existe.
     */
    public static Libro busquedaBinaria(Libro[] libros, String isbn) {
        int inicio = 0;
        int fin = libros.length - 1;

        while (inicio <= fin) {
            int medio = inicio + (fin - inicio) / 2;
            int cmp = isbn.compareTo(libros[medio].getIsbn());

            if (cmp == 0) {
                return libros[medio];     // encontrado
            } else if (cmp < 0) {
                fin = medio - 1;          // buscar en la mitad izquierda
            } else {
                inicio = medio + 1;       // buscar en la mitad derecha
            }
        }
        return null; // no encontrado
    }

    // ══════════════════════════════════════════════════════════════
    // BÚSQUEDA EN BST — Ariel (Lunes Semana 4)
    // ══════════════════════════════════════════════════════════════

    /**
     * Busca un libro por ISBN en un árbol binario de búsqueda (BST) de forma recursiva.
     *
     * <p>Complejidad: O(log n) en el caso promedio (árbol balanceado),
     * O(n) en el peor caso (árbol degenerado, equivalente a lista enlazada).</p>
     *
     * @param nodo Raíz del subárbol donde buscar.
     * @param isbn ISBN exacto del libro buscado.
     * @return El {@link Libro} encontrado, o {@code null} si no existe en el subárbol.
     */
    public static Libro buscarEnBST(NodoBST nodo, String isbn) {
        // Caso base: subárbol vacío → no se encontró el libro
        if (nodo == null) {
            return null;
        }

        int cmp = isbn.compareTo(nodo.getLibro().getIsbn());

        if (cmp == 0) {
            return nodo.getLibro();                        // encontrado
        } else if (cmp < 0) {
            return buscarEnBST(nodo.getIzquierdo(), isbn); // ISBN menor → izquierda
        } else {
            return buscarEnBST(nodo.getDerecho(), isbn);   // ISBN mayor → derecha
        }
    }

    // ══════════════════════════════════════════════════════════════
    // BÚSQUEDA LINEAL DE USUARIO — usada por SistemaBiblioteca
    // ══════════════════════════════════════════════════════════════

    /**
     * Busca un usuario por ID mediante búsqueda lineal sobre la lista de usuarios. O(n).
     *
     * <p>Complejidad: O(n) — recorre la lista hasta encontrar el ID.</p>
     *
     * @param usuarios Lista de usuarios registrados.
     * @param id       ID del usuario a buscar.
     * @return El {@link Usuario} encontrado.
     * @throws UsuarioNoEncontradoException si el ID no existe en la lista.
     */
    public static Usuario busquedaLinealUsuario(ListaEnlazada<Usuario> usuarios, String id) {
        for (int i = 0; i < usuarios.getTamanio(); i++) {
            Usuario u = usuarios.obtener(i);
            if (u.getId().equals(id)) {
                return u;
            }
        }
        throw new UsuarioNoEncontradoException(id);
    }
}