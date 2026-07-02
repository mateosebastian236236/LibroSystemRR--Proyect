package librosystemrr.ui.paneles;

import librosystemrr.excepciones.LibroSystemException;
import librosystemrr.modelos.Libro;
import librosystemrr.sistema.SistemaBiblioteca;
import librosystemrr.tads.ListaEnlazada;
import librosystemrr.ui.dialogos.DialogoLibro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Panel Swing para la gestión del catálogo de libros.
 * Permite buscar, listar y registrar libros, y ordenar el catálogo.
 */
@SuppressWarnings({"serial", "this-escape"})
public class PanelCatalogo extends JPanel {

    private final SistemaBiblioteca sistema;

    private JTable tablaLibros;
    private DefaultTableModel modeloTabla;
    private JTextField campoBusqueda;
    private JComboBox<String> comboBusqueda;
    private JLabel lblResultados;

    private static final String[] COLUMNAS = {
            "ISBN", "Título", "Autor", "Año", "Disponible"
    };

    /**
     * @param sistema Instancia de {@link SistemaBiblioteca}.
     */
    public PanelCatalogo(SistemaBiblioteca sistema) {
        this.sistema = sistema;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        inicializarUI();
        refrescar();
    }

    private void inicializarUI() {
        add(crearPanelSuperior(), BorderLayout.NORTH);
        add(crearPanelCentral(), BorderLayout.CENTER);
        add(crearPanelInferior(), BorderLayout.SOUTH);
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout(10, 8));

        JLabel titulo = new JLabel("Catálogo de Libros");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        titulo.setForeground(new Color(30, 58, 95));

        JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        campoBusqueda = new JTextField(20);
        comboBusqueda = new JComboBox<>(new String[]{"Por título", "Por ISBN"});
        JButton btnBuscar = new JButton("Buscar");
        JButton btnTodos = new JButton("Ver todos");

        btnBuscar.addActionListener(e -> buscar());
        btnTodos.addActionListener(e -> refrescar());
        campoBusqueda.addActionListener(e -> buscar());

        busqueda.add(comboBusqueda);
        busqueda.add(campoBusqueda);
        busqueda.add(btnBuscar);
        busqueda.add(btnTodos);

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(busqueda, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelCentral() {
        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaLibros = new JTable(modeloTabla);
        tablaLibros.setRowHeight(24);
        tablaLibros.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tablaLibros.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tablaLibros.getColumnModel().getColumn(0).setPreferredWidth(110);
        tablaLibros.getColumnModel().getColumn(1).setPreferredWidth(250);
        tablaLibros.getColumnModel().getColumn(2).setPreferredWidth(160);
        tablaLibros.getColumnModel().getColumn(3).setPreferredWidth(55);
        tablaLibros.getColumnModel().getColumn(4).setPreferredWidth(80);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(tablaLibros), BorderLayout.CENTER);

        lblResultados = new JLabel(" ");
        lblResultados.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblResultados.setForeground(Color.DARK_GRAY);
        lblResultados.setBorder(BorderFactory.createEmptyBorder(4, 4, 0, 0));
        panel.add(lblResultados, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        JButton btnNuevo        = new JButton("+ Nuevo libro");
        JButton btnEliminar     = new JButton("🗑 Eliminar libro");
        JButton btnOrdenarTit   = new JButton("Ordenar por título");
        JButton btnOrdenarAut   = new JButton("Ordenar por autor");
        JButton btnRefrescar    = new JButton("⟳ Refrescar");

        btnNuevo.setBackground(new Color(30, 58, 95));
        btnNuevo.setForeground(Color.WHITE);
        btnNuevo.setFocusPainted(false);
        btnEliminar.setBackground(new Color(160, 40, 40));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFocusPainted(false);

        btnNuevo.addActionListener(e -> abrirDialogoNuevoLibro());
        btnEliminar.addActionListener(e -> eliminarLibroSeleccionado());
        btnOrdenarTit.addActionListener(e -> ordenarPorTitulo());
        btnOrdenarAut.addActionListener(e -> ordenarPorAutor());
        btnRefrescar.addActionListener(e -> refrescar());

        panel.add(btnNuevo);
        panel.add(btnEliminar);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(btnOrdenarTit);
        panel.add(btnOrdenarAut);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(btnRefrescar);
        return panel;
    }

    // ══════════════════════════════════════════
    // ACCIONES
    // ══════════════════════════════════════════

    /**
     * Recarga la tabla con todos los libros en orden inorden del BST.
     */
    public void refrescar() {
        modeloTabla.setRowCount(0);
        if (sistema.getCatalogo().estaVacio()) {
            lblResultados.setText("El catálogo está vacío.");
            return;
        }
        try {
            ListaEnlazada<Libro> lista = sistema.getCatalogo().inorden();
            cargarEnTabla(lista);
        } catch (LibroSystemException ex) {
            lblResultados.setText(ex.getMensaje());
        }
    }

    private void buscar() {
        String texto = campoBusqueda.getText().trim();
        if (texto.isEmpty()) { refrescar(); return; }

        modeloTabla.setRowCount(0);
        try {
            if (comboBusqueda.getSelectedIndex() == 0) {
                // Buscar por título
                ListaEnlazada<Libro> resultado = sistema.buscarLibroPorTitulo(texto);
                cargarEnTabla(resultado);
            } else {
                // Buscar por ISBN exacto
                Libro libro = sistema.buscarLibroPorIsbn(texto);
                agregarFilaLibro(libro);
                lblResultados.setText("1 resultado encontrado.");
            }
        } catch (LibroSystemException ex) {
            lblResultados.setText(ex.getMensaje());
        }
    }

    private void ordenarPorTitulo() {
        modeloTabla.setRowCount(0);
        try {
            ListaEnlazada<Libro> ordenada = sistema.ordenarCatalogoPorTitulo();
            cargarEnTabla(ordenada);
            lblResultados.setText("Catálogo ordenado por título (MergeSort).");
        } catch (LibroSystemException ex) {
            lblResultados.setText(ex.getMensaje());
        }
    }

    private void ordenarPorAutor() {
        modeloTabla.setRowCount(0);
        try {
            ListaEnlazada<Libro> ordenada = sistema.ordenarCatalogoPorAutor();
            cargarEnTabla(ordenada);
            lblResultados.setText("Catálogo ordenado por autor (MergeSort).");
        } catch (LibroSystemException ex) {
            lblResultados.setText(ex.getMensaje());
        }
    }

    private void eliminarLibroSeleccionado() {
        int fila = tablaLibros.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un libro en la tabla para eliminarlo.",
                    "Sin selección", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String isbn = (String) modeloTabla.getValueAt(fila, 0);
        String titulo = (String) modeloTabla.getValueAt(fila, 1);
        String disponible = (String) modeloTabla.getValueAt(fila, 4);

        if (disponible.contains("No")) {
            JOptionPane.showMessageDialog(this,
                    "No se puede eliminar \"" + titulo + "\" porque está actualmente prestado.",
                    "Libro no disponible", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el libro \"" + titulo + "\" (ISBN: " + isbn + ")?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            sistema.getCatalogo().eliminar(isbn);
            refrescar();
            lblResultados.setText("Libro \"" + titulo + "\" eliminado del catálogo.");
        }
    }

    private void abrirDialogoNuevoLibro() {
        DialogoLibro dialogo = new DialogoLibro(
                (JFrame) SwingUtilities.getWindowAncestor(this), sistema
        );
        dialogo.setVisible(true);
        if (dialogo.isConfirmado()) refrescar();
    }

    private void cargarEnTabla(ListaEnlazada<Libro> lista) {
        for (int i = 0; i < lista.getTamanio(); i++) {
            agregarFilaLibro(lista.obtener(i));
        }
        lblResultados.setText(lista.getTamanio() + " libro(s) encontrado(s).");
    }

    private void agregarFilaLibro(Libro libro) {
        modeloTabla.addRow(new Object[]{
                libro.getIsbn(),
                libro.getTitulo(),
                libro.getAutor(),
                libro.getAnioPublicacion(),
                libro.isDisponible() ? "✅ Sí" : "❌ No"
        });
    }
}