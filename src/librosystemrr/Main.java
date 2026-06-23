package librosystemrr;

import librosystemrr.modelos.*;
import librosystemrr.sistema.SistemaBiblioteca;
import librosystemrr.ui.VentanaPrincipal;

import javax.swing.*;

/**
 * Punto de entrada del sistema LibroSystemRR.
 * Inicializa el sistema, carga datos de prueba y lanza la interfaz gráfica.
 */
public class Main {

    public static void main(String[] args) {
        // Usar el look and feel del sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Si falla, se usa el look and feel por defecto de Java (Metal)
        }

        // Inicializar sistema y cargar datos de prueba
        SistemaBiblioteca sistema = new SistemaBiblioteca();
        cargarDatosDePrueba(sistema);

        // Lanzar la UI en el Event Dispatch Thread (EDT) de Swing
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal(sistema);
            ventana.mostrar();
        });
    }

    /**
     * Carga datos de prueba en el sistema para facilitar la demostración y defensa.
     * Incluye 5 libros, 3 usuarios (1 Lector, 1 Bibliotecario, 1 Ayudante)
     * y 2 préstamos de prueba.
     *
     * @param sistema Sistema de biblioteca donde se cargarán los datos.
     */
    private static void cargarDatosDePrueba(SistemaBiblioteca sistema) {

        // ── Libros ──
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

        // ── Usuarios ──
        sistema.registrarUsuario(new Lector("L001", "Ana Torres"));
        sistema.registrarUsuario(new Lector("L002", "Carlos Méndez"));
        sistema.registrarUsuario(new Bibliotecario("B001", "María López", "EMP-2024-01"));
        sistema.registrarUsuario(new AyudanteBibliotecario("A001", "Luis Paredes", "EMP-2024-02"));

        // ── Préstamos de prueba ──
        try {
            sistema.registrarPrestamo("L001", "978-0-13-110362-7"); // Ana toma "The C Programming Language"
            sistema.registrarPrestamo("L002", "978-0-201-63361-0"); // Carlos toma "Design Patterns"
        } catch (Exception e) {
            System.err.println("Error al cargar préstamos de prueba: " + e.getMessage());
        }
    }
}