package librosystemrr.ui.paneles;

import librosystemrr.excepciones.LibroSystemException;
import librosystemrr.modelos.Lector;
import librosystemrr.modelos.Usuario;
import librosystemrr.sistema.SistemaBiblioteca;
import librosystemrr.tads.ListaEnlazada;
import librosystemrr.ui.dialogos.DialogoUsuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Panel Swing para la gestión de usuarios del sistema.
 * Permite registrar nuevos usuarios y consultar su información y préstamos activos.
 *
 * <p>Solo interactúa con la capa de negocio a través de {@link SistemaBiblioteca}.</p>
 */
@SuppressWarnings({"serial", "this-escape"})
public class PanelUsuarios extends JPanel {

    private final SistemaBiblioteca sistema;

    // Componentes de la tabla
    private JTable tablaUsuarios;
    private DefaultTableModel modeloTabla;

    // Barra de búsqueda
    private JTextField campoBusqueda;

    // Etiqueta de detalle del usuario seleccionado
    private JLabel lblDetalle;

    /** Columnas de la tabla de usuarios. */
    private static final String[] COLUMNAS = {
            "ID", "Nombre", "Tipo", "Préstamos activos", "Tiene deudas"
    };

    /**
     * Construye el panel de usuarios e inicializa todos los componentes.
     *
     * @param sistema Instancia de {@link SistemaBiblioteca}.
     */
    public PanelUsuarios(SistemaBiblioteca sistema) {
        this.sistema = sistema;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        inicializarUI();
        refrescar();
    }

    // ══════════════════════════════════════════
    // CONSTRUCCIÓN DE LA UI
    // ══════════════════════════════════════════

    private void inicializarUI() {
        add(crearPanelSuperior(), BorderLayout.NORTH);
        add(crearPanelCentral(), BorderLayout.CENTER);
        add(crearPanelInferior(), BorderLayout.SOUTH);
    }

    /**
     * Panel superior: título y barra de búsqueda.
     */
    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout(10, 5));

        JLabel titulo = new JLabel("Gestión de Usuarios");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        titulo.setForeground(new Color(30, 58, 95));

        JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        campoBusqueda = new JTextField(20);
        campoBusqueda.setToolTipText("Buscar usuario por ID o nombre");
        JButton btnBuscar = new JButton("Buscar");
        JButton btnMostrarTodos = new JButton("Mostrar todos");

        btnBuscar.addActionListener(e -> buscarUsuario());
        btnMostrarTodos.addActionListener(e -> refrescar());
        campoBusqueda.addActionListener(e -> buscarUsuario());

        busqueda.add(new JLabel("Buscar: "));
        busqueda.add(campoBusqueda);
        busqueda.add(btnBuscar);
        busqueda.add(btnMostrarTodos);

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(busqueda, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Panel central: tabla de usuarios.
     */
    private JPanel crearPanelCentral() {
        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // tabla de solo lectura
            }
        };

        tablaUsuarios = new JTable(modeloTabla);
        tablaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaUsuarios.setRowHeight(24);
        tablaUsuarios.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tablaUsuarios.setFont(new Font("SansSerif", Font.PLAIN, 12));

        // Ajustar ancho de columnas
        tablaUsuarios.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaUsuarios.getColumnModel().getColumn(1).setPreferredWidth(200);
        tablaUsuarios.getColumnModel().getColumn(2).setPreferredWidth(120);
        tablaUsuarios.getColumnModel().getColumn(3).setPreferredWidth(130);
        tablaUsuarios.getColumnModel().getColumn(4).setPreferredWidth(100);

        // Mostrar detalle al seleccionar una fila
        tablaUsuarios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) mostrarDetalle();
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(tablaUsuarios), BorderLayout.CENTER);

        // Panel de detalle del usuario seleccionado
        lblDetalle = new JLabel(" ");
        lblDetalle.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblDetalle.setForeground(Color.DARK_GRAY);
        lblDetalle.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));
        panel.add(lblDetalle, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Panel inferior: botones de acción.
     */
    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        JButton btnNuevoLector       = new JButton("+ Nuevo Lector");
        JButton btnNuevoBibliotecario = new JButton("+ Nuevo Bibliotecario");
        JButton btnNuevoAyudante     = new JButton("+ Nuevo Ayudante");
        JButton btnRefrescar         = new JButton("⟳ Refrescar");

        btnNuevoLector.setBackground(new Color(40, 120, 80));
        btnNuevoLector.setForeground(Color.WHITE);
        btnNuevoLector.setFocusPainted(false);

        btnNuevoLector.addActionListener(e -> abrirDialogoNuevoUsuario("LECTOR"));
        btnNuevoBibliotecario.addActionListener(e -> abrirDialogoNuevoUsuario("BIBLIOTECARIO"));
        btnNuevoAyudante.addActionListener(e -> abrirDialogoNuevoUsuario("AYUDANTE"));
        btnRefrescar.addActionListener(e -> refrescar());

        panel.add(btnNuevoLector);
        panel.add(btnNuevoBibliotecario);
        panel.add(btnNuevoAyudante);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(btnRefrescar);

        return panel;
    }

    // ══════════════════════════════════════════
    // LÓGICA DE ACCIONES
    // ══════════════════════════════════════════

    /**
     * Recarga la tabla con todos los usuarios registrados en el sistema.
     */
    public void refrescar() {
        modeloTabla.setRowCount(0);
        ListaEnlazada<Usuario> usuarios = sistema.getUsuarios();

        for (int i = 0; i < usuarios.getTamanio(); i++) {
            Usuario u = usuarios.obtener(i);
            String deuda = "-";
            String prestamosActivos = String.valueOf(u.getPrestamosActivos().getTamanio());

            if (u instanceof Lector) {
                Lector lector = (Lector) u;
                deuda = lector.tieneDeudas() ? "⚠ Sí" : "No";
                prestamosActivos += " / " + Lector.LIMITE_PRESTAMOS;
            }

            modeloTabla.addRow(new Object[]{
                    u.getId(),
                    u.getNombre(),
                    u.getTipo(),
                    prestamosActivos,
                    deuda
            });
        }
        lblDetalle.setText(" ");
    }

    /**
     * Busca un usuario por ID o nombre (búsqueda parcial en la tabla).
     */
    private void buscarUsuario() {
        String texto = campoBusqueda.getText().trim().toLowerCase();
        if (texto.isEmpty()) {
            refrescar();
            return;
        }

        modeloTabla.setRowCount(0);
        ListaEnlazada<Usuario> usuarios = sistema.getUsuarios();

        for (int i = 0; i < usuarios.getTamanio(); i++) {
            Usuario u = usuarios.obtener(i);
            if (u.getId().toLowerCase().contains(texto) ||
                    u.getNombre().toLowerCase().contains(texto)) {

                String deuda = "-";
                String prestamosActivos = String.valueOf(u.getPrestamosActivos().getTamanio());
                if (u instanceof Lector) {
                    Lector lector = (Lector) u;
                    deuda = lector.tieneDeudas() ? "⚠ Sí" : "No";
                    prestamosActivos += " / " + Lector.LIMITE_PRESTAMOS;
                }

                modeloTabla.addRow(new Object[]{
                        u.getId(), u.getNombre(), u.getTipo(), prestamosActivos, deuda
                });
            }
        }

        if (modeloTabla.getRowCount() == 0) {
            lblDetalle.setText("No se encontró ningún usuario con: \"" + campoBusqueda.getText().trim() + "\"");
        }
    }

    /**
     * Muestra el detalle del usuario seleccionado en la tabla.
     */
    private void mostrarDetalle() {
        int fila = tablaUsuarios.getSelectedRow();
        if (fila < 0) return;

        String id = (String) modeloTabla.getValueAt(fila, 0);
        try {
            Usuario u = sistema.buscarUsuario(id);
            String detalle = "ID: " + u.getId() + "  |  Nombre: " + u.getNombre() +
                    "  |  Tipo: " + u.getTipo() +
                    "  |  Historial: " + u.getHistorialPrestamos().getTamanio() + " préstamos";
            lblDetalle.setText(detalle);
        } catch (LibroSystemException ex) {
            lblDetalle.setText("No se pudo cargar el detalle del usuario.");
        }
    }

    /**
     * Abre el diálogo para registrar un nuevo usuario del tipo indicado.
     *
     * @param tipo "LECTOR", "BIBLIOTECARIO" o "AYUDANTE".
     */
    private void abrirDialogoNuevoUsuario(String tipo) {
        DialogoUsuario dialogo = new DialogoUsuario(
                (JFrame) SwingUtilities.getWindowAncestor(this), sistema, tipo
        );
        dialogo.setVisible(true);
        if (dialogo.isConfirmado()) {
            refrescar();
        }
    }
}