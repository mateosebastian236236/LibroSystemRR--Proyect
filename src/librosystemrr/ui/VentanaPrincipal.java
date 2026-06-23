package librosystemrr.ui;

import librosystemrr.sistema.SistemaBiblioteca;
import librosystemrr.ui.paneles.PanelAlertas;
import librosystemrr.ui.paneles.PanelCatalogo;
import librosystemrr.ui.paneles.PanelPrestamos;
import librosystemrr.ui.paneles.PanelUsuarios;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal del sistema LibroSystemRR.
 * Contiene un {@link JTabbedPane} con los cuatro paneles del sistema.
 * Es el único punto de entrada de la capa de presentación.
 *
 * <p>Principio de arquitectura: esta clase solo llama a {@link SistemaBiblioteca}.
 * Nunca accede directamente a TADs ni a modelos desde aquí.</p>
 */
public class VentanaPrincipal extends JFrame {

    /** Sistema de biblioteca (capa de negocio). */
    private final SistemaBiblioteca sistema;

    /** Panel de pestañas principal. */
    private JTabbedPane tabbedPane;

    // Paneles del sistema
    private PanelCatalogo panelCatalogo;
    private PanelPrestamos panelPrestamos;
    private PanelUsuarios panelUsuarios;
    private PanelAlertas panelAlertas;

    /**
     * Construye la ventana principal e inicializa todos los paneles.
     *
     * @param sistema Instancia de {@link SistemaBiblioteca} ya cargada con datos.
     */
    public VentanaPrincipal(SistemaBiblioteca sistema) {
        this.sistema = sistema;
        inicializarVentana();
        inicializarPaneles();
        inicializarUI();
    }

    // ══════════════════════════════════════════
    // INICIALIZACIÓN
    // ══════════════════════════════════════════

    /**
     * Configura las propiedades base del JFrame.
     */
    private void inicializarVentana() {
        setTitle("LibroSystemRR — Sistema de Biblioteca");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 650);
        setMinimumSize(new Dimension(800, 550));
        setLocationRelativeTo(null); // centrar en pantalla
        setResizable(true);
    }

    /**
     * Crea los paneles inyectando el sistema de biblioteca en cada uno.
     */
    private void inicializarPaneles() {
        panelCatalogo   = new PanelCatalogo(sistema);
        panelPrestamos  = new PanelPrestamos(sistema);
        panelUsuarios   = new PanelUsuarios(sistema);
        panelAlertas    = new PanelAlertas(sistema);
    }

    /**
     * Construye la interfaz: barra de título, pestañas y barra de estado.
     */
    private void inicializarUI() {
        setLayout(new BorderLayout());

        // ── Encabezado ──
        JPanel encabezado = crearEncabezado();
        add(encabezado, BorderLayout.NORTH);

        // ── Pestañas ──
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.PLAIN, 13));

        tabbedPane.addTab("📚 Catálogo",   panelCatalogo);
        tabbedPane.addTab("🔖 Préstamos",  panelPrestamos);
        tabbedPane.addTab("👤 Usuarios",   panelUsuarios);
        tabbedPane.addTab("⚠️ Alertas",    panelAlertas);

        // Al cambiar de pestaña, refrescar el panel activo
        tabbedPane.addChangeListener(e -> refrescarPanelActivo());

        add(tabbedPane, BorderLayout.CENTER);

        // ── Barra de estado ──
        JPanel barraEstado = crearBarraEstado();
        add(barraEstado, BorderLayout.SOUTH);
    }

    /**
     * Crea el encabezado superior con el nombre del sistema.
     *
     * @return JPanel configurado como encabezado.
     */
    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 58, 95));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel titulo = new JLabel("LibroSystemRR");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel("Sistema de Gestión de Biblioteca — EPN FIS");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitulo.setForeground(new Color(180, 200, 220));

        JPanel textos = new JPanel(new GridLayout(2, 1));
        textos.setOpaque(false);
        textos.add(titulo);
        textos.add(subtitulo);

        panel.add(textos, BorderLayout.WEST);
        return panel;
    }

    /**
     * Crea la barra de estado inferior con información del sistema.
     *
     * @return JPanel configurado como barra de estado.
     */
    private JPanel crearBarraEstado() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 4));
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        JLabel info = new JLabel(
                "Libros: " + sistema.getCatalogo().getTamanio() +
                        "   |   Usuarios: " + sistema.getUsuarios().getTamanio() +
                        "   |   Préstamos: " + sistema.getPrestamos().getTamanio()
        );
        info.setFont(new Font("SansSerif", Font.PLAIN, 11));
        info.setForeground(Color.DARK_GRAY);

        panel.add(info);
        return panel;
    }

    /**
     * Refresca el panel activo al cambiar de pestaña.
     */
    private void refrescarPanelActivo() {
        int indice = tabbedPane.getSelectedIndex();
        switch (indice) {
            case 0: panelCatalogo.refrescar();  break;
            case 1: panelPrestamos.refrescar(); break;
            case 2: panelUsuarios.refrescar();  break;
            case 3: panelAlertas.refrescar();   break;
        }
    }

    /**
     * Hace visible la ventana principal.
     */
    public void mostrar() {
        setVisible(true);
    }
}