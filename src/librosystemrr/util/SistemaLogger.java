package librosystemrr.util;

import java.io.Serializable;
import java.util.Date;

/**
 * Logger centralizado del sistema LibroSystemRR.
 * Implementa el patrón <b>Singleton</b> para garantizar una única instancia
 * en toda la aplicación, transversal a todas las capas.
 *
 * <p>Registra eventos de tipo INFO, WARNING y ERROR en consola.
 * En Semanas 5-7 se puede extender para escribir en archivo.</p>
 */
public class SistemaLogger implements Serializable {

    /** Única instancia del logger (Singleton). */
    private static transient SistemaLogger instancia;

    /** Ruta del archivo de log (extensible en iteraciones futuras). */
    private final String archivoLog;

    /**
     * Constructor privado — impide instanciación externa.
     * Patrón Singleton: solo se llama desde {@link #getInstancia()}.
     */
    private SistemaLogger() {
        this.archivoLog = "librosystemrr.log";
    }

    /**
     * Retorna la única instancia del logger.
     * Thread-safe con inicialización perezosa sincronizada.
     *
     * @return La instancia única de {@link SistemaLogger}.
     */
    public static synchronized SistemaLogger getInstancia() {
        if (instancia == null) {
            instancia = new SistemaLogger();
        }
        return instancia;
    }

    /**
     * Registra un mensaje informativo.
     *
     * @param mensaje Texto descriptivo del evento.
     */
    public void registrarInfo(String mensaje) {
        imprimir("INFO", mensaje);
    }

    /**
     * Registra un error capturado con información de la capa donde ocurrió.
     *
     * @param e     Excepción capturada.
     * @param capa  Nombre de la capa donde se produjo el error (ej. "negocio", "tads").
     */
    public void registrarError(Exception e, String capa) {
        imprimir("ERROR", "[" + capa + "] " + e.getClass().getSimpleName() + ": " + e.getMessage());
    }

    /**
     * Registra una advertencia que no interrumpe el flujo pero requiere atención.
     *
     * @param mensaje Texto descriptivo de la advertencia.
     */
    public void registrarWarning(String mensaje) {
        imprimir("WARNING", mensaje);
    }

    /**
     * Método interno que formatea e imprime el mensaje en consola.
     *
     * @param nivel   Nivel del log (INFO / WARNING / ERROR).
     * @param mensaje Cuerpo del mensaje.
     */
    private void imprimir(String nivel, String mensaje) {
        System.out.println("[" + nivel + " | " + new Date() + "] " + mensaje);
    }

    /**
     * Retorna la ruta del archivo de log configurado.
     *
     * @return Nombre o ruta del archivo de log.
     */
    public String getArchivoLog() {
        return archivoLog;
    }

    /**
     * Garantiza que la deserialización retorne siempre la misma instancia Singleton.
     * Evita que Java Serialization cree una segunda instancia del logger.
     *
     * @return La única instancia existente de {@link SistemaLogger}.
     */
    private Object readResolve() {
        return getInstancia();
    }
}