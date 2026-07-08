package librosystemrr.persistencia;

import librosystemrr.sistema.SistemaBiblioteca;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Gestiona la persistencia del sistema usando serialización Java.
 * Guarda y carga el estado completo de {@link SistemaBiblioteca} en un archivo binario.
 *
 * <p>El archivo se guarda en {@code datos/librosystem.dat} relativo al directorio
 * de ejecución. La carpeta {@code datos/} se crea automáticamente si no existe.</p>
 *
 * <p>No usa bases de datos ni librerías externas — solo {@code java.io}.</p>
 */
public class GestorPersistencia {

    private static final String DIRECTORIO = "datos";
    private static final String ARCHIVO     = DIRECTORIO + "/librosystem.dat";

    /**
     * Guarda el estado completo del sistema en disco.
     * Crea el directorio {@code datos/} si no existe.
     *
     * @param sistema Sistema de biblioteca a persistir.
     * @return {@code true} si se guardó exitosamente, {@code false} si hubo error.
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
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(archivo))) {
            return (SistemaBiblioteca) ois.readObject();
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
}