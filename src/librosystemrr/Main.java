package librosystemrr;

import librosystemrr.modelos.*;
import librosystemrr.persistencia.GestorPersistencia;
import librosystemrr.sistema.SistemaBiblioteca;
import librosystemrr.ui.PanelLogin;
import librosystemrr.ui.VentanaPrincipal;

import javax.swing.*;
import java.util.Date;

/**
 * Punto de entrada del sistema LibroSystemRR.
 * Carga los datos persistidos (o inicializa con datos de prueba si es la primera vez),
 * muestra el login y lanza la interfaz principal.
 */
public class Main {

    public static void main(String[] args) {
        // Nimbus L&F para botones con color correcto
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
            catch (Exception ignored) { }
        }

        // Cargar datos persistidos o inicializar por primera vez
        SistemaBiblioteca sistema;
        if (GestorPersistencia.existenDatos()) {
            sistema = GestorPersistencia.cargar();
            if (sistema == null) {
                sistema = new SistemaBiblioteca();
                cargarDatosDePrueba(sistema);
            }
        } else {
            sistema = new SistemaBiblioteca();
            cargarDatosDePrueba(sistema);
        }

        // Registrar el sistema en GestorPersistencia para guardado automático
        GestorPersistencia.inicializar(sistema);

        // Shutdown hook: guarda aunque el programa sea detenido con el botón stop de IntelliJ
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            GestorPersistencia.guardarActual();
            System.out.println("[LibroSystemRR] Datos guardados al cerrar.");
        }));

        final SistemaBiblioteca sistemaCargado = sistema;

        SwingUtilities.invokeLater(() -> {
            // Mostrar login
            PanelLogin login = new PanelLogin(sistemaCargado);
            login.setVisible(true);

            if (!login.isLoginExitoso()) {
                System.exit(0);
            }

            // Login exitoso: abrir ventana principal
            VentanaPrincipal ventana = new VentanaPrincipal(
                    sistemaCargado,
                    () -> GestorPersistencia.guardar(sistemaCargado)
            );
            ventana.mostrar();
        });
    }

    /**
     * Carga datos iniciales la primera vez que se ejecuta el sistema.
     *
     * @param sistema Sistema de biblioteca a poblar.
     */
    private static void cargarDatosDePrueba(SistemaBiblioteca sistema) {

        // Libros
        sistema.registrarLibro(new Libro("978-0-13-110362-7",
                "The C Programming Language", "Brian W. Kernighan", 1988));
        sistema.registrarLibro(new Libro("978-0-201-63361-0",
                "Design Patterns", "Gang of Four", 1994));
        sistema.registrarLibro(new Libro("978-0-13-468599-1",
                "Clean Code", "Robert C. Martin", 2008));
        sistema.registrarLibro(new Libro("978-0-596-51774-8",
                "JavaScript: The Good Parts", "Douglas Crockford", 2008));
        sistema.registrarLibro(new Libro("978-0-13-235088-4",
                "The Pragmatic Programmer", "David Thomas", 2019));

        // Usuarios (id, nombre, contrasena)
        sistema.registrarUsuario(new Lector("L001", "Ana Torres", "ana123"));
        sistema.registrarUsuario(new Lector("L002", "Carlos Mendez", "carlos123"));
        sistema.registrarUsuario(new Lector("L003", "Pedro Alvarado", "pedro123"));
        sistema.registrarUsuario(new Bibliotecario("B001", "Maria Lopez", "EMP-2024-01", "biblio123"));
        sistema.registrarUsuario(new AyudanteBibliotecario("A001", "Luis Paredes", "EMP-2024-02", "ayudante123"));

        // Salas de lectura
        sistema.registrarSala(new SalaLectura("S01", "Sala A - General", 10));
        sistema.registrarSala(new SalaLectura("S02", "Sala B - Silenciosa", 6));
        sistema.registrarSala(new SalaLectura("S03", "Sala C - Grupal", 20));

        // Computadoras
        sistema.registrarComputadora(new Computadora("C01", 1));
        sistema.registrarComputadora(new Computadora("C02", 2));
        sistema.registrarComputadora(new Computadora("C03", 3));
        sistema.registrarComputadora(new Computadora("C04", 4));
        sistema.registrarComputadora(new Computadora("C05", 5));

       
    }
}