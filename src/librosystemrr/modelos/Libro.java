package librosystemrr.modelos;

import librosystemrr.interfaces.Ordenable;

/**
 * Representa un libro en el catálogo de la biblioteca.
 * Implementa {@link Ordenable} para permitir su comparación y ordenamiento
 * por título dentro de los algoritmos de la Semana 4.
 *
 * <p>El ISBN actúa como clave única e inmutable del libro.</p>
 */
import java.io.Serializable;

public class Libro implements Ordenable<Libro> {

    /** ISBN único del libro. Clave de búsqueda en el BST. */
    private String isbn;

    /** Título del libro. */
    private String titulo;

    /** Nombre del autor. */
    private String autor;

    /** Año de publicación. */
    private int anioPublicacion;

    /** Indica si el libro está disponible para préstamo. */
    private boolean disponible;

    /**
     * Construye un libro con todos sus atributos. Por defecto está disponible.
     *
     * @param isbn           ISBN único del libro.
     * @param titulo         Título del libro.
     * @param autor          Autor del libro.
     * @param anioPublicacion Año de publicación.
     */
    public Libro(String isbn, String titulo, String autor, int anioPublicacion) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.anioPublicacion = anioPublicacion;
        this.disponible = true;
    }

    // ══════════════════════════════════════════
    // GETTERS Y SETTERS
    // ══════════════════════════════════════════

    /** @return ISBN del libro. */
    public String getIsbn() { return isbn; }

    /** @return Título del libro. */
    public String getTitulo() { return titulo; }

    /** @return Autor del libro. */
    public String getAutor() { return autor; }

    /** @return Año de publicación. */
    public int getAnioPublicacion() { return anioPublicacion; }

    /**
     * Indica si el libro está disponible para préstamo.
     *
     * @return {@code true} si está disponible.
     */
    public boolean isDisponible() { return disponible; }

    /**
     * Establece la disponibilidad del libro.
     *
     * @param disponible {@code true} para marcar disponible, {@code false} para prestado.
     */
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    // ══════════════════════════════════════════
    // Ordenable<Libro>
    // ══════════════════════════════════════════

    /**
     * Compara este libro con otro por título (orden alfabético, insensible a mayúsculas).
     * Usado por MergeSort y QuickSort en AlgoritmosOrdenamiento.
     *
     * @param otro El libro con el que se compara.
     * @return Negativo si este título va antes, 0 si son iguales, positivo si va después.
     */
    @Override
    public int compareTo(Libro otro) {
        return this.titulo.compareToIgnoreCase(otro.titulo);
    }

    /**
     * Compara este libro con otro por autor (orden alfabético, insensible a mayúsculas).
     * Método auxiliar para ordenarPorAutor en AlgoritmosOrdenamiento.
     *
     * @param otro El libro con el que se compara.
     * @return Negativo si este autor va antes, 0 si son iguales, positivo si va después.
     */
    public int compareToAutor(Libro otro) {
        return this.autor.compareToIgnoreCase(otro.autor);
    }

    @Override
    public String toString() {
        return "Libro{isbn='" + isbn + "', titulo='" + titulo +
                "', autor='" + autor + "', anio=" + anioPublicacion +
                ", disponible=" + disponible + "}";
    }
}