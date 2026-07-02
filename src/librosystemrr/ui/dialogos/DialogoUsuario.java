package librosystemrr.ui.dialogos;

import librosystemrr.excepciones.LibroSystemException;
import librosystemrr.modelos.AyudanteBibliotecario;
import librosystemrr.modelos.Bibliotecario;
import librosystemrr.modelos.Lector;
import librosystemrr.sistema.SistemaBiblioteca;

import javax.swing.*;
import java.awt.*;

/**
 * Diálogo modal para registrar un nuevo usuario en el sistema.
 * Soporta los tres tipos: LECTOR, BIBLIOTECARIO y AYUDANTE.
 *
 * <p>Aplica patrón Factory Method: crea la instancia correcta de usuario
 * según el tipo seleccionado.</p>
 */
@SuppressWarnings({"serial", "this-escape"})
public class DialogoUsuario extends JDialog {

    private final SistemaBiblioteca sistema;
    private final String tipo;

    private JTextField campoId;
    private JTextField campoNombre;
    private JTextField campoCodigo; // solo para Bibliotecario y Ayudante

    private boolean confirmado = false;

    /**
     * Construye el diálogo para registrar un usuario del tipo indicado.
     *
     * @param padre   Ventana padre del diálogo.
     * @param sistema Sistema de biblioteca donde se registrará el usuario.
     * @param tipo    Tipo de usuario: "LECTOR", "BIBLIOTECARIO" o "AYUDANTE".
     */
    public DialogoUsuario(JFrame padre, SistemaBiblioteca sistema, String tipo) {
        super(padre, "Nuevo " + capitalizar(tipo), true);
        this.sistema = sistema;
        this.tipo = tipo;
        inicializarUI();
        pack();
        setMinimumSize(new Dimension(380, 200));
        setLocationRelativeTo(padre);
    }

    // ══════════════════════════════════════════
    // CONSTRUCCIÓN DE LA UI
    // ══════════════════════════════════════════

    private void inicializarUI() {
        setLayout(new BorderLayout(10, 10));

        // ── Formulario ──
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Campo: ID
        gbc.gridx = 0; gbc.gridy = 0;
        formulario.add(new JLabel("ID de usuario:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        campoId = new JTextField(15);
        formulario.add(campoId, gbc);

        // Campo: Nombre
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formulario.add(new JLabel("Nombre completo:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        campoNombre = new JTextField(15);
        formulario.add(campoNombre, gbc);

        // Campo: Código de empleado (solo si no es Lector)
        if (!tipo.equals("LECTOR")) {
            gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formulario.add(new JLabel("Código de empleado:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            campoCodigo = new JTextField(15);
            formulario.add(campoCodigo, gbc);
        }

        add(formulario, BorderLayout.CENTER);

        // ── Botones ──
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.setBackground(new Color(30, 100, 180));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);

        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose());

        botones.add(btnCancelar);
        botones.add(btnGuardar);
        add(botones, BorderLayout.SOUTH);
    }

    // ══════════════════════════════════════════
    // LÓGICA DE GUARDADO
    // ══════════════════════════════════════════

    /**
     * Valida los campos y registra el usuario en el sistema.
     * Aplica el patrón Factory Method para crear el tipo correcto.
     */
    private void guardar() {
        String id = campoId.getText().trim();
        String nombre = campoNombre.getText().trim();

        if (id.isEmpty() || nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El ID y el nombre son obligatorios.",
                    "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            switch (tipo) {
                case "LECTOR":
                    sistema.registrarUsuario(new Lector(id, nombre));
                    break;

                case "BIBLIOTECARIO":
                    String codigoBib = campoCodigo.getText().trim();
                    if (codigoBib.isEmpty()) {
                        mostrarErrorCodigo(); return;
                    }
                    sistema.registrarUsuario(new Bibliotecario(id, nombre, codigoBib));
                    break;

                case "AYUDANTE":
                    String codigoAy = campoCodigo.getText().trim();
                    if (codigoAy.isEmpty()) {
                        mostrarErrorCodigo(); return;
                    }
                    sistema.registrarUsuario(new AyudanteBibliotecario(id, nombre, codigoAy));
                    break;
            }

            confirmado = true;
            JOptionPane.showMessageDialog(this,
                    capitalizar(tipo) + " registrado exitosamente.",
                    "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (LibroSystemException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMensaje(), "Error al registrar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarErrorCodigo() {
        JOptionPane.showMessageDialog(this,
                "El código de empleado es obligatorio.",
                "Campo incompleto", JOptionPane.WARNING_MESSAGE);
    }

    // ══════════════════════════════════════════
    // GETTER
    // ══════════════════════════════════════════

    /**
     * Indica si el usuario confirmó el registro (pulsó "Guardar" exitosamente).
     *
     * @return {@code true} si el registro fue exitoso.
     */
    public boolean isConfirmado() { return confirmado; }

    // ══════════════════════════════════════════
    // UTILIDAD
    // ══════════════════════════════════════════

    private static String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return texto.charAt(0) + texto.substring(1).toLowerCase();
    }
}