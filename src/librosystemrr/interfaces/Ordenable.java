package librosystemrr.interfaces;

/**
 * Interfaz genérica que impone un orden natural sobre los objetos que la implementen.
 * Equivalente funcional a {@link Comparable}, implementada de forma propia
 * para cumplir la restricción de no usar librerías externas del paquete java.util.
 *
 * @param <T> Tipo del objeto a comparar.
 */
public interface Ordenable<T> {

    /**
     * Compara este objeto con otro del mismo tipo.
     *
     * @param otro El objeto con el que se compara.
     * @return Valor negativo si {@code this < otro}, 0 si son iguales,
     *         valor positivo si {@code this > otro}.
     */
    int compareTo(T otro);
}