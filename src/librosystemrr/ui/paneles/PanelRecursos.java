package librosystemrr.ui.paneles;

import librosystemrr.modelos.Computadora;
import librosystemrr.modelos.SalaLectura;
import librosystemrr.modelos.SolicitudComputadora;
import librosystemrr.modelos.SolicitudSala;
import librosystemrr.persistencia.GestorPersistencia;
import librosystemrr.sistema.SistemaBiblioteca;
import librosystemrr.tads.ListaEnlazada;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Panel Swing para la gestion de recursos de la biblioteca:
 * salas de lectura y computadoras.
 *
 * <p>Ambos recursos usan el TAD {@link librosystemrr.tads.Cola} internamente
 * para gestionar las colas de espera (FIFO).</p>
 */
@SuppressWarnings({"serial", "this-escape"})
public class PanelRecursos extends JPanel {

    private final SistemaBiblioteca sistema;

    private JTable tablaSalas;
    private DefaultTableModel modeloSalas;

    private JTable tablaComputadoras;
    private DefaultTableModel modeloComputadoras;

    private JLabel lblMensaje;

    private static final String[] COL_SALAS = {
            "ID", "N°", "Nombre", "Capacidad", "Estado", "Usuario actual", "En espera"
    };
    private static final String[] COL_COMPUTADORAS = {
            "ID", "N°", "Estado", "Usuario actual", "En espera"
    };

    /**
     * @param sistema Instancia de {@link SistemaBiblioteca}.
     */
    public PanelRecursos(SistemaBiblioteca sistema) {
        this.sistema = sistema;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        inicializarUI();
        refrescar();
    }

    private void inicializarUI() {
        JLabel titulo = new JLabel("Recursos de la Biblioteca");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        titulo.setForeground(new Color(30, 58, 95));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                crearPanelSalas(), crearPanelComputadoras());
        split.setResizeWeight(0.5);
        split.setDividerSize(8);
        add(split, BorderLayout.CENTER);

        lblMensaje = new JLabel(" ");
        lblMensaje.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblMensaje.setForeground(new Color(30, 100, 60));
        lblMensaje.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        add(lblMensaje, BorderLayout.SOUTH);
    }

    // ══════════════════════════════════════════
    // PANEL SALAS
    // ══════════════════════════════════════════

    private JPanel crearPanelSalas() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(30, 58, 95)), "Salas de Lectura"));

        modeloSalas = new DefaultTableModel(COL_SALAS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaSalas = new JTable(modeloSalas);
        tablaSalas.setRowHeight(24);
        tablaSalas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaSalas.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tablaSalas.setFont(new Font("SansSerif", Font.PLAIN, 12));

        panel.add(new JScrollPane(tablaSalas), BorderLayout.CENTER);
        panel.add(crearBotonesSalas(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearBotonesSalas() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        JButton btnSolicitar = crearBoton("Solicitar sala", new Color(30, 100, 60));
        JButton btnLiberar   = crearBoton("Liberar sala",   new Color(140, 60, 20));
        JButton btnRefrescar = new JButton("Refrescar");

        btnSolicitar.addActionListener(e -> solicitarSala());
        btnLiberar.addActionListener(e -> liberarSala());
        btnRefrescar.addActionListener(e -> refrescar());

        p.add(btnSolicitar);
        p.add(btnLiberar);
        p.add(btnRefrescar);
        return p;
    }

    // ══════════════════════════════════════════
    // PANEL COMPUTADORAS
    // ══════════════════════════════════════════

    private JPanel crearPanelComputadoras() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(30, 58, 95)), "Computadoras"));

        modeloComputadoras = new DefaultTableModel(COL_COMPUTADORAS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaComputadoras = new JTable(modeloComputadoras);
        tablaComputadoras.setRowHeight(24);
        tablaComputadoras.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaComputadoras.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tablaComputadoras.setFont(new Font("SansSerif", Font.PLAIN, 12));

        panel.add(new JScrollPane(tablaComputadoras), BorderLayout.CENTER);
        panel.add(crearBotonesComputadoras(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearBotonesComputadoras() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        JButton btnSolicitar = crearBoton("Solicitar computadora", new Color(30, 100, 60));
        JButton btnLiberar   = crearBoton("Liberar computadora",   new Color(140, 60, 20));
        JButton btnRefrescar = new JButton("Refrescar");

        btnSolicitar.addActionListener(e -> solicitarComputadora());
        btnLiberar.addActionListener(e -> liberarComputadora());
        btnRefrescar.addActionListener(e -> refrescar());

        p.add(btnSolicitar);
        p.add(btnLiberar);
        p.add(btnRefrescar);
        return p;
    }

    // ══════════════════════════════════════════
    // ACCIONES — SALAS
    // ══════════════════════════════════════════

    private void solicitarSala() {
        int fila = tablaSalas.getSelectedRow();
        if (fila < 0) { mostrarMensaje("Selecciona una sala en la tabla.", false); return; }
        String idSala = (String) modeloSalas.getValueAt(fila, 0);

        String idUsuario = JOptionPane.showInputDialog(this, "ID del usuario:");
        if (idUsuario == null || idUsuario.trim().isEmpty()) return;

        String nombreUsuario = JOptionPane.showInputDialog(this, "Nombre del usuario:");
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) return;

        String horasStr = JOptionPane.showInputDialog(this, "Horas de uso (1-4):", "2");
        if (horasStr == null) return;
        int horas;
        try { horas = Integer.parseInt(horasStr.trim()); }
        catch (NumberFormatException e) { mostrarMensaje("Ingresa un numero valido de horas.", false); return; }

        try {
            SolicitudSala sol = sistema.solicitarSala(
                    idUsuario.trim(), nombreUsuario.trim(), horas);
            String msg = sol.getEstado().equals("ASIGNADA")
                    ? "Sala asignada exitosamente a " + nombreUsuario.trim() + " (ID solicitud: " + sol.getId() + ")"
                    : "Todas las salas ocupadas. " + nombreUsuario.trim() + " en lista de espera (ID: " + sol.getId() + ")";
            mostrarMensaje(msg, sol.getEstado().equals("ASIGNADA"));
            GestorPersistencia.guardarActual();
            refrescar();
        } catch (Exception ex) {
            mostrarMensaje(ex.getMessage(), false);
        }
    }

    private void liberarSala() {
        int fila = tablaSalas.getSelectedRow();
        if (fila < 0) { mostrarMensaje("Selecciona una sala en la tabla.", false); return; }
        String idSala = (String) modeloSalas.getValueAt(fila, 0);
        String estado = (String) modeloSalas.getValueAt(fila, 4);

        if (estado.contains("Libre")) {
            mostrarMensaje("La sala ya esta libre.", false);
            return;
        }

        sistema.liberarSala(idSala);
        GestorPersistencia.guardarActual();
        mostrarMensaje("Sala liberada correctamente.", true);
        refrescar();
    }

    // ══════════════════════════════════════════
    // ACCIONES — COMPUTADORAS
    // ══════════════════════════════════════════

    private void solicitarComputadora() {
        int fila = tablaComputadoras.getSelectedRow();
        if (fila < 0) { mostrarMensaje("Selecciona una computadora en la tabla.", false); return; }
        String idComp = (String) modeloComputadoras.getValueAt(fila, 0);

        String idUsuario = JOptionPane.showInputDialog(this, "ID del usuario:");
        if (idUsuario == null || idUsuario.trim().isEmpty()) return;

        String nombreUsuario = JOptionPane.showInputDialog(this, "Nombre del usuario:");
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) return;

        String minsStr = JOptionPane.showInputDialog(this, "Minutos de uso (30-120):", "60");
        if (minsStr == null) return;
        int minutos;
        try { minutos = Integer.parseInt(minsStr.trim()); }
        catch (NumberFormatException e) { mostrarMensaje("Ingresa un numero valido de minutos.", false); return; }

        try {
            SolicitudComputadora sol = sistema.solicitarComputadora(
                    idUsuario.trim(), nombreUsuario.trim(), minutos);
            String msg = sol.getEstado().equals("ASIGNADA")
                    ? "Computadora asignada a " + nombreUsuario.trim() + " (ID solicitud: " + sol.getId() + ")"
                    : "Todas las computadoras ocupadas. " + nombreUsuario.trim() + " en lista de espera.";
            mostrarMensaje(msg, sol.getEstado().equals("ASIGNADA"));
            GestorPersistencia.guardarActual();
            refrescar();
        } catch (Exception ex) {
            mostrarMensaje(ex.getMessage(), false);
        }
    }

    private void liberarComputadora() {
        int fila = tablaComputadoras.getSelectedRow();
        if (fila < 0) { mostrarMensaje("Selecciona una computadora en la tabla.", false); return; }
        String idComp = (String) modeloComputadoras.getValueAt(fila, 0);
        String estado = (String) modeloComputadoras.getValueAt(fila, 2);

        if (estado.contains("Disponible")) {
            mostrarMensaje("La computadora ya esta disponible.", false);
            return;
        }

        sistema.liberarComputadora(idComp);
        GestorPersistencia.guardarActual();
        mostrarMensaje("Computadora liberada correctamente.", true);
        refrescar();
    }

    // ══════════════════════════════════════════
    // REFRESCO DE DATOS
    // ══════════════════════════════════════════

    /**
     * Recarga ambas tablas con los datos actuales del sistema.
     */
    public void refrescar() {
        // Salas
        modeloSalas.setRowCount(0);
        ListaEnlazada<SalaLectura> salas = sistema.getSalas();
        for (int i = 0; i < salas.getTamanio(); i++) {
            SalaLectura s = salas.obtener(i);
            String usuarioActual = s.getSolicitudActual() != null
                    ? s.getSolicitudActual().getNombreUsuario() : "—";
            modeloSalas.addRow(new Object[]{
                    s.getId(),
                    s.getNumero(),
                    s.getNombre(),
                    s.getCapacidad() + " personas",
                    s.isOcupada() ? "Ocupada" : "Libre",
                    usuarioActual,
                    s.getColaEspera().getTamanio()
            });
        }

        // Computadoras
        modeloComputadoras.setRowCount(0);
        ListaEnlazada<Computadora> computadoras = sistema.getComputadoras();
        for (int i = 0; i < computadoras.getTamanio(); i++) {
            Computadora c = computadoras.obtener(i);
            String usuarioActual = c.getSolicitudActual() != null
                    ? c.getSolicitudActual().getNombreUsuario() : "—";
            modeloComputadoras.addRow(new Object[]{
                    c.getId(),
                    c.getNumero(),
                    c.isDisponible() ? "Disponible" : "En uso",
                    usuarioActual,
                    "—"
            });
        }
    }

    // ══════════════════════════════════════════
    // UTILIDADES
    // ══════════════════════════════════════════

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        return btn;
    }

    private void mostrarMensaje(String texto, boolean exitoso) {
        lblMensaje.setForeground(exitoso ? new Color(20, 110, 50) : new Color(160, 40, 20));
        lblMensaje.setText(texto);
    }
}