package librosystemrr.ui.paneles;

import librosystemrr.modelos.Multa;
import librosystemrr.modelos.Prestamo;
import librosystemrr.sistema.SistemaBiblioteca;
import librosystemrr.tads.ListaEnlazada;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.SimpleDateFormat;

/**
 * Panel Swing para el módulo de alertas y multas.
 * Muestra los préstamos vencidos, calcula días de retraso y el monto de multa pendiente.
 *
 * <p>Solo interactúa con la capa de negocio a través de {@link SistemaBiblioteca}.</p>
 */
@SuppressWarnings({"serial", "this-escape"})
public class PanelAlertas extends JPanel {

    private final SistemaBiblioteca sistema;

    private JTable tablaVencidos;
    private DefaultTableModel modeloTabla;

    private JLabel lblTotalVencidos;
    private JLabel lblMontoTotal;
    private JLabel lblResumen;

    private static final String[] COLUMNAS = {
            "ID Préstamo", "Libro", "Usuario", "Fecha vencimiento", "Días retraso", "Multa estimada"
    };

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * Construye el panel de alertas.
     *
     * @param sistema Instancia de {@link SistemaBiblioteca}.
     */
    public PanelAlertas(SistemaBiblioteca sistema) {
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
        add(crearPanelResumen(), BorderLayout.SOUTH);
    }

    /**
     * Panel superior: título y botón de escaneo.
     */
    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout(10, 5));

        JLabel titulo = new JLabel("⚠️  Alertas — Préstamos Vencidos");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        titulo.setForeground(new Color(180, 60, 20));

        JPanel controles = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        JButton btnEscanear = new JButton("⟳ Escanear ahora");
        btnEscanear.setBackground(new Color(180, 60, 20));
        btnEscanear.setForeground(Color.WHITE);
        btnEscanear.setFocusPainted(false);
        btnEscanear.setToolTipText("Busca todos los préstamos cuya fecha de devolución ya venció");
        btnEscanear.addActionListener(e -> refrescar());

        lblTotalVencidos = new JLabel("Préstamos vencidos: —");
        lblTotalVencidos.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblTotalVencidos.setForeground(new Color(180, 60, 20));

        controles.add(btnEscanear);
        controles.add(Box.createHorizontalStrut(20));
        controles.add(lblTotalVencidos);

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(controles, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Panel central: tabla de préstamos vencidos con filas coloreadas por gravedad.
     */
    private JPanel crearPanelCentral() {
        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaVencidos = new JTable(modeloTabla) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer,
                                             int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    // Colorear filas según días de retraso (columna 4)
                    try {
                        int dias = Integer.parseInt(modeloTabla.getValueAt(row, 4).toString());
                        if (dias > 14) {
                            comp.setBackground(new Color(255, 200, 200)); // rojo claro
                        } else if (dias > 7) {
                            comp.setBackground(new Color(255, 235, 180)); // naranja claro
                        } else {
                            comp.setBackground(new Color(255, 250, 200)); // amarillo claro
                        }
                    } catch (NumberFormatException e) {
                        comp.setBackground(Color.WHITE);
                    }
                }
                return comp;
            }
        };

        tablaVencidos.setRowHeight(24);
        tablaVencidos.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tablaVencidos.setFont(new Font("SansSerif", Font.PLAIN, 12));

        // Centrar columnas numéricas
        DefaultTableCellRenderer centroCeldas = new DefaultTableCellRenderer();
        centroCeldas.setHorizontalAlignment(JLabel.CENTER);
        tablaVencidos.getColumnModel().getColumn(4).setCellRenderer(centroCeldas);
        tablaVencidos.getColumnModel().getColumn(5).setCellRenderer(centroCeldas);

        // Anchos de columna
        tablaVencidos.getColumnModel().getColumn(0).setPreferredWidth(100);
        tablaVencidos.getColumnModel().getColumn(1).setPreferredWidth(220);
        tablaVencidos.getColumnModel().getColumn(2).setPreferredWidth(160);
        tablaVencidos.getColumnModel().getColumn(3).setPreferredWidth(130);
        tablaVencidos.getColumnModel().getColumn(4).setPreferredWidth(100);
        tablaVencidos.getColumnModel().getColumn(5).setPreferredWidth(110);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(tablaVencidos), BorderLayout.CENTER);

        // Leyenda de colores
        panel.add(crearLeyenda(), BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Leyenda visual con el significado de los colores de las filas.
     */
    private JPanel crearLeyenda() {
        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 4));
        leyenda.setBorder(BorderFactory.createTitledBorder("Leyenda"));

        leyenda.add(crearChipLeyenda("1–7 días", new Color(255, 250, 200)));
        leyenda.add(crearChipLeyenda("8–14 días", new Color(255, 235, 180)));
        leyenda.add(crearChipLeyenda("+14 días", new Color(255, 200, 200)));
        return leyenda;
    }

    private JLabel crearChipLeyenda(String texto, Color color) {
        JLabel chip = new JLabel("  " + texto + "  ");
        chip.setOpaque(true);
        chip.setBackground(color);
        chip.setFont(new Font("SansSerif", Font.PLAIN, 11));
        chip.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        return chip;
    }

    /**
     * Panel inferior: totales de multas y resumen.
     */
    private JPanel crearPanelResumen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(8, 5, 5, 5)
        ));

        lblMontoTotal = new JLabel("Monto total de multas estimadas: $0.00");
        lblMontoTotal.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblMontoTotal.setForeground(new Color(140, 40, 10));

        lblResumen = new JLabel("Sin préstamos vencidos en este momento.");
        lblResumen.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblResumen.setForeground(Color.DARK_GRAY);

        panel.add(lblMontoTotal, BorderLayout.NORTH);
        panel.add(lblResumen, BorderLayout.SOUTH);
        return panel;
    }

    // ══════════════════════════════════════════
    // LÓGICA DE ACTUALIZACIÓN
    // ══════════════════════════════════════════

    /**
     * Escanea el sistema en busca de préstamos vencidos y actualiza la tabla.
     * Llama a {@link SistemaBiblioteca#generarAlertas()} para obtener la lista.
     */
    public void refrescar() {
        modeloTabla.setRowCount(0);
        ListaEnlazada<Prestamo> vencidos = sistema.generarAlertas();

        double montoTotal = 0.0;

        for (int i = 0; i < vencidos.getTamanio(); i++) {
            Prestamo p = vencidos.obtener(i);
            int diasRetraso = p.getDiasRetraso();
            double multa = diasRetraso * Multa.MONTO_POR_DIA;
            montoTotal += multa;

            modeloTabla.addRow(new Object[]{
                    p.getId(),
                    p.getLibro().getTitulo(),
                    p.getUsuario().getNombre(),
                    SDF.format(p.getFechaDevolucion()),
                    diasRetraso,
                    String.format("$%.2f", multa)
            });
        }

        int total = vencidos.getTamanio();
        lblTotalVencidos.setText("Préstamos vencidos: " + total);
        lblMontoTotal.setText(String.format("Monto total de multas estimadas: $%.2f", montoTotal));

        if (total == 0) {
            lblResumen.setText("✅  Sin préstamos vencidos en este momento.");
            lblResumen.setForeground(new Color(30, 120, 60));
        } else {
            lblResumen.setText("⚠  Se encontraron " + total + " préstamos vencidos. " +
                    "Contactar a los usuarios correspondientes.");
            lblResumen.setForeground(new Color(180, 60, 20));
        }
    }
}