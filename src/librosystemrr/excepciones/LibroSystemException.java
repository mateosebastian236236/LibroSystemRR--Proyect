package librosystemrr.excepciones;

import java.util.Date;

/**
 * Clase base abstracta para todas las excepciones personalizadas del sistema LibroSystemRR.
 * Extiende {@link RuntimeException} para ser unchecked, lo que simplifica
 * el manejo en capas superiores.
 */
public abstract class LibroSystemException extends RuntimeException {

    private final String mensaje;
    private final Date timestamp;

    /**
     * Construye la excepción con un mensaje descriptivo y registra el momento exacto.
     *
     * @param mensaje Descripción del error ocurrido.
     */
    public LibroSystemException(String mensaje) {
        super(mensaje);
        this.mensaje = mensaje;
        this.timestamp = new Date();
    }

    /**
     * Retorna el mensaje descriptivo del error.
     *
     * @return Cadena con la descripción del error.
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * Retorna la fecha y hora en que ocurrió la excepción.
     *
     * @return {@link Date} con el timestamp de la excepción.
     */
    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "[" + this.getClass().getSimpleName() + " | " + timestamp + "] " + mensaje;
    }
}