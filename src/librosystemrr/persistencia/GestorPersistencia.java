package librosystemrr.persistencia;

import librosystemrr.sistema.SistemaBiblioteca;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Gestiona la persistencia del sistema usando serialización Java.
 * Guarda y carga el estado completo de {@link SistemaBiblioteca} en un archivo binario.
 *
 * <p>El archivo se guarda en la carpeta del usuario:
 * {@code [home]/.librosystemrr/datos.dat} — ruta fija que nunca cambia
 * sin importar desde dónde se ejecute el programa.</p>
 */
public class GestorPersistencia {

    /** Carpeta de datos en el directorio home del usuario. */
    private static final String DIRECTORIO =
            System.getProperty("user.home") + File.separator + ".librosystemrr";

    /** Ruta completa al archivo de datos. */
    private static final String ARCHIVO = DIRECTORIO + File.separator + "datos.dat";

    /** Referencia estática al sistema activo para guardado desde cualquier panel. */
    private static SistemaBiblioteca sistemaActual;

    /**
     * Registra el sistema activo. Llamar una sola vez al iniciar el programa.
     *
     * @param sistema El sistema de biblioteca a persistir.
     */
    public static void inicializar(SistemaBiblioteca sistema) {
        sistemaActual = sistema;
    }

    /**
     * Guarda el sistema activo en disco.
     * Llamar después de cada operación que modifique datos.
     *
     * @return {@code true} si se guardó exitosamente.
     */
    public static boolean guardarActual() {
        if (sistemaActual == null) return false;
        return guardar(sistemaActual);
    }

    /**
     * Guarda un sistema específico en disco.
     * Crea el directorio si no existe.
     *
     * @param sistema Sistema de biblioteca a persistir.
     * @return {@code true} si se guardó exitosamente.
     */
    public static boolean guardar(SistemaBiblioteca sistema) {
        try {
            Files.createDirectories(Paths.get(DIRECTORIO));
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(ARCHIVO))) {
                oos.writeObject(sistema);
            }
            return true;
        } catch (IOException e) {
            System.err.println("[GestorPersistencia] Error al guardar: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Carga el estado del sistema desde disco.
     * Si el archivo no existe o está corrupto, retorna {@code null}.
     *
     * @return El {@link SistemaBiblioteca} cargado, o {@code null} si no hay datos previos.
     */
    public static SistemaBiblioteca cargar() {
        File archivo = new File(ARCHIVO);
        if (!archivo.exists()) {
            System.out.println("[GestorPersistencia] No hay datos previos. Iniciando con datos de prueba.");
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(archivo))) {
            SistemaBiblioteca sistema = (SistemaBiblioteca) ois.readObject();
            System.out.println("[GestorPersistencia] Datos cargados desde: " + ARCHIVO);
            return sistema;
        } catch (InvalidClassException e) {
            System.err.println("[GestorPersistencia] Archivo incompatible (clases modificadas). " +
                    "Eliminando y reiniciando con datos de prueba.");
            new File(ARCHIVO).delete();
            return null;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[GestorPersistencia] Error al cargar: " + e.getMessage());
            return null;
        }
    }

    /**
     * Indica si existe un archivo de datos guardado previamente.
     *
     * @return {@code true} si el archivo de persistencia existe.
     */
    public static boolean existenDatos() {
        return new File(ARCHIVO).exists();
    }

    /**
     * Retorna la ruta completa al archivo de datos (útil para debug).
     *
     * @return Ruta absoluta del archivo.
     */
    public static String getRutaArchivo() {
        return ARCHIVO;
    }
}