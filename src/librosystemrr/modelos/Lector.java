package librosystemrr.modelos;

/**
 * Representa a un lector de la biblioteca. Extiende {@link Usuario} y agrega
 * las validaciones de negocio propias de un usuario que puede solicitar préstamos.
 *
 * <p>Un lector no puede tener más de {@link #LIMITE_PRESTAMOS} préstamos activos
 * ni puede solicitar nuevos préstamos si tiene deudas pendientes.</p>
 */
import java.io.Serializable;

public class Lector extends Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Máximo de préstamos activos permitidos para un lector. */
    public static final int LIMITE_PRESTAMOS = 3;

    /**
     * Construye un lector con su ID y nombre.
     *
     * @param id     Identificador único del lector.
     * @param nombre Nombre completo del lector.
     */
    public Lector(String id, String nombre, String contrasena) {
        super(id, nombre, contrasena);
    }

    // ══════════════════════════════════════════
    // Template Method
    // ══════════════════════════════════════════

    /**
     * Retorna el tipo de usuario.
     *
     * @return La cadena "LECTOR".
     */
    @Override
    public String getTipo() {
        return "LECTOR";
    }

    // ══════════════════════════════════════════
    // MÉTODOS DE NEGOCIO
    // ══════════════════════════════════════════

    /**
     * Indica si el lector puede solicitar un nuevo préstamo.
     * Condición: préstamos activos menores al límite permitido.
     *
     * @return {@code true} si puede solicitar préstamos.
     */
    public boolean puedePrestar() {
        return getPrestamosActivos().getTamanio() < LIMITE_PRESTAMOS;
    }

    /**
     * Indica si el lector tiene multas pendientes sin pagar.
     * Recorre el historial de préstamos sin destruir la Pila (usando arreglo auxiliar).
     *
     * @return {@code true} si tiene al menos una multa sin pagar.
     */
    public boolean tieneDeudas() {
        // Usamos toArray() del historial (via getCabeza) para no destruir la Pila
        int total = getHistorialPrestamos().getTamanio();
        if (total == 0) return false;

        // Recorrer la pila sin destruirla usando toArray de la ListaEnlazada interna
        // La Pila expone getTope() pero no tiene iterador — usamos un arreglo auxiliar
        Prestamo[] temporal = new Prestamo[total];
        for (int i = 0; i < total; i++) {
            temporal[i] = getHistorialPrestamos().desapilar();
        }
        boolean tieneDeuda = false;
        // Restaurar la pila en el mismo orden (apilar al revés)
        for (int i = total - 1; i >= 0; i--) {
            if (temporal[i].getMulta() != null && !temporal[i].getMulta().isPagada()) {
                tieneDeuda = true;
            }
            getHistorialPrestamos().apilar(temporal[i]);
        }
        return tieneDeuda;
    }

    @Override
    public String toString() {
        return "Lector{id='" + getId() + "', nombre='" + getNombre() +
                "', prestamosActivos=" + getPrestamosActivos().getTamanio() +
                "/" + LIMITE_PRESTAMOS +
                ", tieneDeudas=" + tieneDeudas() + "}";
    }
}