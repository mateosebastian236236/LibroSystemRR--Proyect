package librosystemrr.ui.paneles;

import librosystemrr.excepciones.LibroSystemException;
import librosystemrr.modelos.Prestamo;
import librosystemrr.sistema.SistemaBiblioteca;
import librosystemrr.tads.ListaEnlazada;
import librosystemrr.ui.dialogos.DialogoPrestamo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;

/**
 * Panel Swing para el módulo de préstamos.
 * Permite registrar nuevos préstamos y procesar devoluciones.
 */
@SuppressWarnings({"serial", "this-escape"})
public class PanelPrestamos extends JPanel {

    private final SistemaBiblioteca sistema;

    private JTable tablaPrestamos;
    private DefaultTableModel modeloTabla;
    private JLabel lblResumen;

    private static final String[] COLUMNAS = {
            "ID", "Libro", "Usuario", "Fecha préstamo", "Fecha devolución", "Estado", "Multa"
    };
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * @param sistema Instancia de {@link SistemaBiblioteca}.
     */
    public PanelPrestamos(SistemaBiblioteca sistema) {
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
        JPanel panel = new JPanel(new BorderLayout());
        JLabel titulo = new JLabel("Gestión de Préstamos");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        titulo.setForeground(new Color(30, 58, 95));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        panel.add(titulo, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelCentral() {
        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaPrestamos = new JTable(modeloTabla);
        tablaPrestamos.setRowHeight(24);
        tablaPrestamos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPrestamos.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tablaPrestamos.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tablaPrestamos.getColumnModel().getColumn(0).setPreferredWidth(70);
        tablaPrestamos.getColumnModel().getColumn(1).setPreferredWidth(200);
        tablaPrestamos.getColumnModel().getColumn(2).setPreferredWidth(140);
        tablaPrestamos.getColumnModel().getColumn(3).setPreferredWidth(105);
        tablaPrestamos.getColumnModel().getColumn(4).setPreferredWidth(105);
        tablaPrestamos.getColumnModel().getColumn(5).setPreferredWidth(90);
        tablaPrestamos.getColumnModel().getColumn(6).setPreferredWidth(80);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(tablaPrestamos), BorderLayout.CENTER);

        lblResumen = new JLabel(" ");
        lblResumen.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblResumen.setForeground(Color.DARK_GRAY);
        lblResumen.setBorder(BorderFactory.createEmptyBorder(4, 4, 0, 0));
        panel.add(lblResumen, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        JButton btnNuevo     = new JButton("+ Nuevo préstamo");
        JButton btnDevolver  = new JButton("✔ Registrar devolución");
        JButton btnPagarMulta = new JButton("💲 Pagar multa");
        JButton btnRefrescar = new JButton("⟳ Refrescar");

        btnNuevo.setBackground(new Color(30, 58, 95));
        btnNuevo.setForeground(Color.WHITE);
        btnNuevo.setFocusPainted(false);
        btnDevolver.setBackground(new Color(40, 100, 60));
        btnDevolver.setForeground(Color.WHITE);
        btnDevolver.setFocusPainted(false);
        btnPagarMulta.setBackground(new Color(140, 80, 20));
        btnPagarMulta.setForeground(Color.WHITE);
        btnPagarMulta.setFocusPainted(false);

        btnNuevo.addActionListener(e -> abrirDialogoNuevoPrestamo());
        btnDevolver.addActionListener(e -> procesarDevolucion());
        btnPagarMulta.addActionListener(e -> pagarMultaSeleccionada());
        btnRefrescar.addActionListener(e -> refrescar());

        panel.add(btnNuevo);
        panel.add(btnDevolver);
        panel.add(btnPagarMulta);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(btnRefrescar);
        return panel;
    }

    // ══════════════════════════════════════════
    // ACCIONES
    // ══════════════════════════════════════════

    /**
     * Recarga la tabla con todos los préstamos del sistema.
     */
    public void refrescar() {
        modeloTabla.setRowCount(0);
        ListaEnlazada<Prestamo> prestamos = sistema.getPrestamos();
        int activos = 0;

        for (int i = 0; i < prestamos.getTamanio(); i++) {
            Prestamo p = prestamos.obtener(i);
            String estado;
            if (p.isDevuelto()) {
                estado = "Devuelto";
            } else if (p.isVencido()) {
                estado = "⚠ Vencido";
            } else {
                estado = "Activo";
                activos++;
            }

            String multa = "-";
            if (p.getMulta() != null) {
                multa = "$" + String.format("%.2f", p.getMulta().getMonto()) +
                        (p.getMulta().isPagada() ? " ✔" : " ⚠");
            }

            modeloTabla.addRow(new Object[]{
                    p.getId(),
                    p.getLibro().getTitulo(),
                    p.getUsuario().getNombre(),
                    SDF.format(p.getFechaPrestamo()),
                    SDF.format(p.getFechaDevolucion()),
                    estado,
                    multa
            });
        }
        lblResumen.setText("Total: " + prestamos.getTamanio() + " préstamos | Activos: " + activos);
    }

    private void pagarMultaSeleccionada() {
        int fila = tablaPrestamos.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un préstamo en la tabla para pagar su multa.",
                    "Sin selección", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String idPrestamo = (String) modeloTabla.getValueAt(fila, 0);
        String multaTexto = (String) modeloTabla.getValueAt(fila, 6);

        if (multaTexto.equals("-")) {
            JOptionPane.showMessageDialog(this,
                    "Este préstamo no tiene multa asociada.",
                    "Sin multa", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (multaTexto.contains("✔")) {
            JOptionPane.showMessageDialog(this,
                    "La multa de este préstamo ya fue pagada.",
                    "Multa pagada", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Buscar el préstamo en el sistema y pagar la multa
        for (int i = 0; i < sistema.getPrestamos().getTamanio(); i++) {
            librosystemrr.modelos.Prestamo p = sistema.getPrestamos().obtener(i);
            if (p.getId().equals(idPrestamo) && p.getMulta() != null && !p.getMulta().isPagada()) {
                p.getMulta().pagar();
                JOptionPane.showMessageDialog(this,
                        "Multa de $" + String.format("%.2f", p.getMulta().getMonto()) + " pagada exitosamente.",
                        "Pago registrado", JOptionPane.INFORMATION_MESSAGE);
                refrescar();
                return;
            }
        }
    }

    private void abrirDialogoNuevoPrestamo() {
        DialogoPrestamo dialogo = new DialogoPrestamo(
                (JFrame) SwingUtilities.getWindowAncestor(this), sistema
        );
        dialogo.setVisible(true);
        if (dialogo.isConfirmado()) refrescar();
    }

    private void procesarDevolucion() {
        int fila = tablaPrestamos.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un préstamo en la tabla para procesar la devolución.",
                    "Sin selección", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String idPrestamo = (String) modeloTabla.getValueAt(fila, 0);
        String estado = (String) modeloTabla.getValueAt(fila, 5);

        if (estado.equals("Devuelto")) {
            JOptionPane.showMessageDialog(this,
                    "Este préstamo ya fue devuelto.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Prestamo procesado = sistema.procesarDevolucion(idPrestamo);
            if (procesado.getMulta() != null) {
                JOptionPane.showMessageDialog(this,
                        "Devolución registrada con retraso de " + procesado.getDiasRetraso() + " día(s).\n" +
                                "Multa generada: $" + String.format("%.2f", procesado.getMulta().getMonto()),
                        "Devolución con multa", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Devolución registrada exitosamente. Sin multa.",
                        "Devolución exitosa", JOptionPane.INFORMATION_MESSAGE);
            }
            refrescar();
        } catch (LibroSystemException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMensaje(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}