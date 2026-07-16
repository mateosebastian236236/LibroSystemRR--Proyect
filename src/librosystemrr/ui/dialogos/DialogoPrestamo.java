package librosystemrr.ui.dialogos;

import librosystemrr.excepciones.LibroSystemException;
import librosystemrr.modelos.Libro;
import librosystemrr.modelos.Prestamo;
import librosystemrr.modelos.Usuario;
import librosystemrr.persistencia.GestorPersistencia;
import librosystemrr.sistema.SistemaBiblioteca;
import librosystemrr.tads.ListaEnlazada;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;

/**
 * Diálogo para registrar un préstamo.
 * Búsqueda de libro con dropdown tipo navegador web (sin scrollbar).
 * Búsqueda de usuario por cédula con nombre automático.
 */
public class DialogoPrestamo extends JDialog {

    private final SistemaBiblioteca sistema;

    // Libro
    private JTextField campoBusqueda;
    private JWindow dropdownWindow;
    private JPanel panelDropdown;
    private String isbnSeleccionado;
    private JLabel lblLibroSeleccionado;

    // Usuario
    private JTextField campoCedula;
    private JLabel lblNombreUsuario;

    // General
    private JLabel lblFeedback;
    private boolean confirmado = false;

    // Colores del dropdown
    private static final Color COLOR_HOVER    = new Color(232, 240, 254);
    private static final Color COLOR_BORDE    = new Color(200, 210, 220);
    private static final Color COLOR_TITULO   = new Color(30, 58, 95);
    private static final Color VERDE          = new Color(20, 110, 50);
    private static final Color ROJO           = new Color(160, 40, 20);

    /**
     * @param padre   Ventana padre.
     * @param sistema Sistema de biblioteca.
     */
    public DialogoPrestamo(JFrame padre, SistemaBiblioteca sistema) {
        super(padre, "Registrar nuevo préstamo", true);
        this.sistema = sistema;
        inicializarUI();
        setSize(460, 330);
        setResizable(false);
        setLocationRelativeTo(padre);

        // Cerrar dropdown si se hace clic fuera
        addWindowListener(new WindowAdapter() {
            @Override public void windowDeactivated(WindowEvent e) { ocultarDropdown(); }
            @Override public void windowClosing(WindowEvent e)     { ocultarDropdown(); }
        });
    }

    // ══════════════════════════════════════════
    // CONSTRUCCIÓN DE LA UI
    // ══════════════════════════════════════════

    private void inicializarUI() {
        setLayout(new BorderLayout());
        getRootPane().setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel contenido = new JPanel(new GridBagLayout());
        contenido.setBackground(Color.WHITE);
        contenido.setBorder(new EmptyBorder(20, 24, 10, 24));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;
        g.weightx = 1.0;
        g.insets = new Insets(3, 0, 3, 0);

        // ── LIBRO ──────────────────────────────────────────
        g.gridx = 0; g.gridy = 0;
        contenido.add(seccionLabel("LIBRO"), g);

        g.gridy = 1;
        contenido.add(fieldLabel("Buscar por título:"), g);

        g.gridy = 2;
        campoBusqueda = new JTextField();
        campoBusqueda.setFont(new Font("SansSerif", Font.PLAIN, 13));
        campoBusqueda.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1),
                new EmptyBorder(6, 8, 6, 8)));
        campoBusqueda.setPreferredSize(new Dimension(0, 34));
        contenido.add(campoBusqueda, g);

        g.gridy = 3;
        lblLibroSeleccionado = new JLabel("  ");
        lblLibroSeleccionado.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblLibroSeleccionado.setForeground(VERDE);
        contenido.add(lblLibroSeleccionado, g);

        // ── USUARIO ────────────────────────────────────────
        g.gridy = 4;
        JLabel sepUsuario = seccionLabel("USUARIO");
        sepUsuario.setBorder(new EmptyBorder(10, 0, 0, 0));
        contenido.add(sepUsuario, g);

        g.gridy = 5;
        contenido.add(fieldLabel("Cédula (10 dígitos):"), g);

        g.gridy = 6;
        campoCedula = new JTextField();
        campoCedula.setFont(new Font("SansSerif", Font.PLAIN, 13));
        campoCedula.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1),
                new EmptyBorder(6, 8, 6, 8)));
        campoCedula.setPreferredSize(new Dimension(0, 34));
        contenido.add(campoCedula, g);

        g.gridy = 7;
        lblNombreUsuario = new JLabel("  ");
        lblNombreUsuario.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblNombreUsuario.setForeground(VERDE);
        contenido.add(lblNombreUsuario, g);

        // ── FEEDBACK ───────────────────────────────────────
        g.gridy = 8;
        lblFeedback = new JLabel(" ");
        lblFeedback.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblFeedback.setForeground(ROJO);
        contenido.add(lblFeedback, g);

        add(contenido, BorderLayout.CENTER);

        // ── BOTONES ────────────────────────────────────────
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        panelBotones.setBackground(new Color(245, 247, 250));
        panelBotones.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDE));

        JButton btnCancelar  = new JButton("Cancelar");
        JButton btnRegistrar = new JButton("Registrar préstamo");
        btnRegistrar.setBackground(COLOR_TITULO);
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setOpaque(true);
        btnRegistrar.setBorderPainted(false);
        btnRegistrar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnRegistrar.setPreferredSize(new Dimension(165, 32));
        btnCancelar.setPreferredSize(new Dimension(90, 32));

        btnRegistrar.addActionListener(e -> registrar());
        btnCancelar.addActionListener(e -> { ocultarDropdown(); dispose(); });

        panelBotones.add(btnCancelar);
        panelBotones.add(btnRegistrar);
        add(panelBotones, BorderLayout.SOUTH);

        // ── LISTENERS ──────────────────────────────────────
        campoBusqueda.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { actualizarDropdown(); }
            public void removeUpdate(DocumentEvent e)  { actualizarDropdown(); }
            public void changedUpdate(DocumentEvent e) {}
        });

        campoBusqueda.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                // Delay para permitir clic en dropdown
                SwingUtilities.invokeLater(() -> {
                    if (dropdownWindow != null && !dropdownWindow.isFocused()) {
                        ocultarDropdown();
                    }
                });
            }
        });

        campoBusqueda.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) ocultarDropdown();
            }
        });

        campoCedula.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { buscarUsuario(); }
            public void removeUpdate(DocumentEvent e)  { buscarUsuario(); }
            public void changedUpdate(DocumentEvent e) {}
        });
    }

    // ══════════════════════════════════════════
    // DROPDOWN TIPO NAVEGADOR
    // ══════════════════════════════════════════

    /**
     * Actualiza el dropdown de sugerencias de libros al escribir.
     */
    private void actualizarDropdown() {
        String texto = campoBusqueda.getText().trim();

        // Si el usuario acaba de seleccionar un libro, no volver a mostrar sugerencias
        if (isbnSeleccionado != null && campoBusqueda.getText().equals(lblLibroSeleccionado.getText().replace(" ✓ seleccionado", "").trim())) {
            return;
        }

        isbnSeleccionado = null;
        lblLibroSeleccionado.setText(" ");
        lblFeedback.setText(" ");

        if (texto.length() < 2) { ocultarDropdown(); return; }

        ListaEnlazada<Libro> resultados = sistema.buscarSugerenciasLibro(texto);
        if (resultados.getTamanio() == 0) { ocultarDropdown(); return; }

        mostrarDropdown(resultados);
    }

    /**
     * Muestra el dropdown flotante debajo del campo de búsqueda.
     * Aspecto similar al autocompletado de navegadores web.
     */
    private void mostrarDropdown(ListaEnlazada<Libro> libros) {
        if (dropdownWindow == null) {
            dropdownWindow = new JWindow(this);
            panelDropdown = new JPanel();
            panelDropdown.setLayout(new BoxLayout(panelDropdown, BoxLayout.Y_AXIS));
            panelDropdown.setBackground(Color.WHITE);
            panelDropdown.setBorder(BorderFactory.createLineBorder(new Color(180, 195, 210), 1));
            dropdownWindow.add(panelDropdown);
        }

        panelDropdown.removeAll();

        int maxItems = Math.min(libros.getTamanio(), 6);
        for (int i = 0; i < maxItems; i++) {
            Libro libro = libros.obtener(i);
            panelDropdown.add(crearItemDropdown(libro));
            if (i < maxItems - 1) {
                JSeparator sep = new JSeparator();
                sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                sep.setForeground(new Color(235, 238, 242));
                panelDropdown.add(sep);
            }
        }

        // Posicionar exactamente debajo del campo
        try {
            Point pos = campoBusqueda.getLocationOnScreen();
            int ancho = campoBusqueda.getWidth();
            int alto  = maxItems * 44 + 2;

            dropdownWindow.setBounds(pos.x, pos.y + campoBusqueda.getHeight(), ancho, alto);
            dropdownWindow.setVisible(true);
            dropdownWindow.toFront();
        } catch (IllegalComponentStateException ex) {
            // Componente aún no visible
        }

        panelDropdown.revalidate();
        panelDropdown.repaint();
    }

    /**
     * Crea un ítem del dropdown con hover effect, título, autor y estado.
     */
    private JPanel crearItemDropdown(Libro libro) {
        JPanel item = new JPanel(new BorderLayout(8, 0));
        item.setBackground(Color.WHITE);
        item.setBorder(new EmptyBorder(8, 12, 8, 12));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Textos
        JLabel lblTitulo = new JLabel(libro.getTitulo());
        lblTitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblTitulo.setForeground(new Color(30, 30, 30));

        JLabel lblAutor = new JLabel(libro.getAutor() + "  ·  " + libro.getAnioPublicacion());
        lblAutor.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblAutor.setForeground(new Color(130, 140, 155));

        // Estado (disponible / prestado)
        boolean disponible = libro.isDisponible();
        JLabel lblEstado = new JLabel(disponible ? "Disponible" : "Prestado");
        lblEstado.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblEstado.setForeground(disponible ? VERDE : ROJO);
        lblEstado.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 1));
        textos.setOpaque(false);
        textos.add(lblTitulo);
        textos.add(lblAutor);

        item.add(textos,    BorderLayout.CENTER);
        item.add(lblEstado, BorderLayout.EAST);

        // Hover effect
        item.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                item.setBackground(COLOR_HOVER);
                textos.setBackground(COLOR_HOVER);
            }
            @Override public void mouseExited(MouseEvent e) {
                item.setBackground(Color.WHITE);
                textos.setBackground(Color.WHITE);
            }
            @Override public void mouseClicked(MouseEvent e) {
                seleccionarLibro(libro);
            }
        });

        return item;
    }

    private void ocultarDropdown() {
        if (dropdownWindow != null) dropdownWindow.setVisible(false);
    }

    /**
     * Confirma la selección de un libro del dropdown.
     */
    private void seleccionarLibro(Libro libro) {
        ocultarDropdown();
        isbnSeleccionado = libro.getIsbn();
        // Evitar que el DocumentListener vuelva a abrir el dropdown
        campoBusqueda.getDocument().removeDocumentListener(null);
        campoBusqueda.setText(libro.getTitulo());
        lblLibroSeleccionado.setText("ISBN: " + libro.getIsbn()
                + (libro.isDisponible() ? "  ✓ Disponible" : "  ✗ Actualmente prestado"));
        lblLibroSeleccionado.setForeground(libro.isDisponible() ? VERDE : ROJO);
        campoCedula.requestFocus();
    }

    // ══════════════════════════════════════════
    // BÚSQUEDA DE USUARIO POR CÉDULA
    // ══════════════════════════════════════════

    private void buscarUsuario() {
        String cedula = campoCedula.getText().trim();
        if (cedula.length() == 10 && cedula.matches("\\d{10}")) {
            try {
                Usuario u = sistema.buscarUsuario(cedula);
                lblNombreUsuario.setText(u.getNombre() + "  —  " + u.getTipo());
                lblNombreUsuario.setForeground(VERDE);
            } catch (Exception ex) {
                lblNombreUsuario.setText("Cédula no encontrada en el sistema");
                lblNombreUsuario.setForeground(ROJO);
            }
        } else if (cedula.isEmpty()) {
            lblNombreUsuario.setText("  ");
        } else {
            lblNombreUsuario.setText(cedula.length() + " / 10 dígitos");
            lblNombreUsuario.setForeground(new Color(140, 100, 20));
        }
    }

    // ══════════════════════════════════════════
    // REGISTRO
    // ══════════════════════════════════════════

    private void registrar() {
        String cedula = campoCedula.getText().trim();
        lblFeedback.setText(" ");

        if (isbnSeleccionado == null || isbnSeleccionado.isEmpty()) {
            lblFeedback.setText("Selecciona un libro de la lista de sugerencias.");
            campoBusqueda.requestFocus();
            return;
        }
        if (!Usuario.esCedulaValida(cedula)) {
            lblFeedback.setText("Ingresa una cédula válida de 10 dígitos.");
            campoCedula.requestFocus();
            return;
        }

        try {
            Prestamo p = sistema.registrarPrestamo(cedula, isbnSeleccionado);
            GestorPersistencia.guardarActual();
            confirmado = true;
            JOptionPane.showMessageDialog(this,
                    "Préstamo registrado exitosamente.\n" +
                            "ID: " + p.getId() + "\n" +
                            "Libro: " + p.getLibro().getTitulo() + "\n" +
                            "Usuario: " + p.getUsuario().getNombre() + "\n" +
                            "Devolución: " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(p.getFechaDevolucion()),
                    "Préstamo creado", JOptionPane.INFORMATION_MESSAGE);
            ocultarDropdown();
            dispose();
        } catch (LibroSystemException ex) {
            lblFeedback.setText(ex.getMensaje());
        } catch (Exception ex) {
            lblFeedback.setText(ex.getMessage());
        }
    }

    // ══════════════════════════════════════════
    // HELPERS DE ESTILO
    // ══════════════════════════════════════════

    private JLabel seccionLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        lbl.setForeground(new Color(100, 120, 145));
        return lbl;
    }

    private JLabel fieldLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setForeground(new Color(50, 60, 75));
        return lbl;
    }

    /** @return {@code true} si el préstamo fue registrado exitosamente. */
    public boolean isConfirmado() { return confirmado; }
}