package librosystemrr.ui;

import librosystemrr.sistema.SistemaBiblioteca;
import librosystemrr.ui.paneles.PanelAlertas;
import librosystemrr.ui.paneles.PanelCatalogo;
import librosystemrr.ui.paneles.PanelPrestamos;
import librosystemrr.ui.paneles.PanelRecursos;
import librosystemrr.ui.paneles.PanelUsuarios;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Ventana principal del sistema LibroSystemRR.
 * Contiene un {@link JTabbedPane} con los cinco paneles del sistema.
 * Guarda los datos automáticamente al cerrar la ventana.
 */
@SuppressWarnings({"serial", "this-escape"})
public class VentanaPrincipal extends JFrame {

    private final SistemaBiblioteca sistema;
    private final Runnable onClose;

    private JTabbedPane tabbedPane;
    private JLabel lblEstado;

    private PanelCatalogo   panelCatalogo;
    private PanelPrestamos  panelPrestamos;
    private PanelUsuarios   panelUsuarios;
    private PanelAlertas    panelAlertas;
    private PanelRecursos   panelRecursos;

    /**
     * Construye la ventana principal.
     *
     * @param sistema Sistema de biblioteca ya cargado con datos.
     * @param onClose Runnable ejecutado al cerrar (para guardar datos).
     */
    public VentanaPrincipal(SistemaBiblioteca sistema, Runnable onClose) {
        this.sistema = sistema;
        this.onClose = onClose;
        inicializarVentana();
        inicializarPaneles();
        inicializarUI();
    }

    private void inicializarVentana() {
        setTitle("LibroSystemRR - Sistema de Biblioteca");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1000, 680);
        setMinimumSize(new Dimension(800, 550));
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onClose.run();
                dispose();
                System.exit(0);
            }
        });
    }

    private void inicializarPaneles() {
        panelCatalogo  = new PanelCatalogo(sistema);
        panelPrestamos = new PanelPrestamos(sistema);
        panelUsuarios  = new PanelUsuarios(sistema);
        panelAlertas   = new PanelAlertas(sistema);
        panelRecursos  = new PanelRecursos(sistema);
    }

    private void inicializarUI() {
        setLayout(new BorderLayout());
        add(crearEncabezado(), BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabbedPane.addTab("Catalogo",   panelCatalogo);
        tabbedPane.addTab("Prestamos",  panelPrestamos);
        tabbedPane.addTab("Usuarios",   panelUsuarios);
        tabbedPane.addTab("Alertas",    panelAlertas);
        tabbedPane.addTab("Recursos",   panelRecursos);

        tabbedPane.addChangeListener(e -> {
            refrescarPanelActivo();
            actualizarBarraEstado();
        });

        add(tabbedPane, BorderLayout.CENTER);
        add(crearBarraEstado(), BorderLayout.SOUTH);
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 58, 95));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel titulo = new JLabel("LibroSystemRR");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel("Sistema de Gestion de Biblioteca - EPN FIS");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitulo.setForeground(new Color(180, 200, 220));

        JPanel textos = new JPanel(new GridLayout(2, 1));
        textos.setOpaque(false);
        textos.add(titulo);
        textos.add(subtitulo);

        panel.add(textos, BorderLayout.WEST);
        return panel;
    }

    private JPanel crearBarraEstado() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 4));
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        lblEstado = new JLabel(textoEstado());
        lblEstado.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblEstado.setForeground(Color.DARK_GRAY);

        panel.add(lblEstado);
        return panel;
    }

    /**
     * Actualiza el texto de la barra de estado con los contadores actuales.
     */
    public void actualizarBarraEstado() {
        if (lblEstado != null) lblEstado.setText(textoEstado());
    }

    private String textoEstado() {
        return "Libros: " + sistema.getCatalogo().getTamanio() +
                "   |   Usuarios: " + sistema.getUsuarios().getTamanio() +
                "   |   Prestamos: " + sistema.getPrestamos().getTamanio() +
                "   |   Salas: " + sistema.getSalas().getTamanio() +
                "   |   Computadoras: " + sistema.getComputadoras().getTamanio();
    }

    private void refrescarPanelActivo() {
        int i = tabbedPane.getSelectedIndex();
        switch (i) {
            case 0: panelCatalogo.refrescar();  break;
            case 1: panelPrestamos.refrescar(); break;
            case 2: panelUsuarios.refrescar();  break;
            case 3: panelAlertas.refrescar();   break;
            case 4: panelRecursos.refrescar();  break;
        }
    }

    /**
     * Hace visible la ventana principal.
     */
    public void mostrar() {
        setVisible(true);
    }
}