package librosystemrr.ui.dialogos;

import librosystemrr.excepciones.LibroSystemException;
import librosystemrr.modelos.AyudanteBibliotecario;
import librosystemrr.modelos.Bibliotecario;
import librosystemrr.modelos.Lector;
import librosystemrr.modelos.Usuario;
import librosystemrr.persistencia.GestorPersistencia;
import librosystemrr.sistema.SistemaBiblioteca;

import javax.swing.*;
import java.awt.*;

/**
 * Diálogo modal para registrar un nuevo usuario en el sistema.
 * La cédula (10 dígitos numéricos) es el identificador único.
 */
public class DialogoUsuario extends JDialog {

    private final SistemaBiblioteca sistema;
    private final String tipo;

    private JTextField campoCedula;
    private JTextField campoNombre;
    private JPasswordField campoContrasena;
    private JTextField campoCodigo;
    private JLabel lblValidacionCedula;

    private boolean confirmado = false;

    /**
     * @param padre   Ventana padre.
     * @param sistema Sistema de biblioteca.
     * @param tipo    "LECTOR", "BIBLIOTECARIO" o "AYUDANTE".
     */
    public DialogoUsuario(JFrame padre, SistemaBiblioteca sistema, String tipo) {
        super(padre, "Nuevo " + capitalizar(tipo), true);
        this.sistema = sistema;
        this.tipo = tipo;
        inicializarUI();
        pack();
        setMinimumSize(new Dimension(400, 240));
        setLocationRelativeTo(padre);
    }

    private void inicializarUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 4, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int fila = 0;

        // Cédula
        gbc.gridx = 0; gbc.gridy = fila; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formulario.add(new JLabel("Cédula (10 dígitos):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        campoCedula = new JTextField(15);
        campoCedula.setToolTipText("Número de cédula de identidad — 10 dígitos");
        formulario.add(campoCedula, gbc);

        // Validación visual de cédula
        fila++;
        gbc.gridx = 1; gbc.gridy = fila; gbc.fill = GridBagConstraints.HORIZONTAL;
        lblValidacionCedula = new JLabel(" ");
        lblValidacionCedula.setFont(new Font("SansSerif", Font.ITALIC, 10));
        lblValidacionCedula.setForeground(new Color(160, 40, 20));
        formulario.add(lblValidacionCedula, gbc);

        // Nombre
        fila++;
        gbc.gridx = 0; gbc.gridy = fila; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formulario.add(new JLabel("Nombre completo:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        campoNombre = new JTextField(15);
        formulario.add(campoNombre, gbc);

        // Contraseña
        fila++;
        gbc.gridx = 0; gbc.gridy = fila; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formulario.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        campoContrasena = new JPasswordField(15);
        formulario.add(campoContrasena, gbc);

        // Código de empleado (solo Bibliotecario y Ayudante)
        if (!tipo.equals("LECTOR")) {
            fila++;
            gbc.gridx = 0; gbc.gridy = fila; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formulario.add(new JLabel("Código de empleado:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            campoCodigo = new JTextField(15);
            formulario.add(campoCodigo, gbc);
        }

        add(formulario, BorderLayout.CENTER);

        // Validar cédula en tiempo real
        campoCedula.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validarCedulaEnTiempoReal(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validarCedulaEnTiempoReal(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {}
        });

        // Botones
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnGuardar  = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.setBackground(new Color(30, 100, 60));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setOpaque(true);
        btnGuardar.setBorderPainted(false);

        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose());

        botones.add(btnCancelar);
        botones.add(btnGuardar);
        add(botones, BorderLayout.SOUTH);
    }

    private void validarCedulaEnTiempoReal() {
        String cedula = campoCedula.getText().trim();
        if (cedula.isEmpty()) {
            lblValidacionCedula.setText(" ");
        } else if (!cedula.matches("\\d{10}")) {
            lblValidacionCedula.setText("Debe tener exactamente 10 dígitos numéricos (" + cedula.length() + "/10)");
            lblValidacionCedula.setForeground(new Color(160, 40, 20));
        } else {
            // Check uniqueness
            boolean existe = false;
            for (int i = 0; i < sistema.getUsuarios().getTamanio(); i++) {
                if (sistema.getUsuarios().obtener(i).getCedula().equals(cedula)) {
                    existe = true; break;
                }
            }
            if (existe) {
                lblValidacionCedula.setText("Esta cédula ya está registrada en el sistema");
                lblValidacionCedula.setForeground(new Color(160, 40, 20));
            } else {
                lblValidacionCedula.setText("Cédula válida ✓");
                lblValidacionCedula.setForeground(new Color(20, 110, 50));
            }
        }
    }

    private void guardar() {
        String cedula    = campoCedula.getText().trim();
        String nombre    = campoNombre.getText().trim();
        String contrasena = new String(campoContrasena.getPassword()).trim();

        if (cedula.isEmpty() || nombre.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La cédula, el nombre y la contraseña son obligatorios.",
                    "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!Usuario.esCedulaValida(cedula)) {
            JOptionPane.showMessageDialog(this, "La cédula debe tener exactamente 10 dígitos numéricos.",
                    "Cédula inválida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            switch (tipo) {
                case "LECTOR":
                    sistema.registrarUsuario(new Lector(cedula, nombre, contrasena));
                    break;
                case "BIBLIOTECARIO":
                    String codigoBib = campoCodigo.getText().trim();
                    if (codigoBib.isEmpty()) { mostrarErrorCodigo(); return; }
                    sistema.registrarUsuario(new Bibliotecario(cedula, nombre, codigoBib, contrasena));
                    break;
                case "AYUDANTE":
                    String codigoAy = campoCodigo.getText().trim();
                    if (codigoAy.isEmpty()) { mostrarErrorCodigo(); return; }
                    sistema.registrarUsuario(new AyudanteBibliotecario(cedula, nombre, codigoAy, contrasena));
                    break;
            }
            GestorPersistencia.guardarActual();
            confirmado = true;
            JOptionPane.showMessageDialog(this, capitalizar(tipo) + " registrado exitosamente.",
                    "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de validación", JOptionPane.WARNING_MESSAGE);
        } catch (LibroSystemException ex) {
            JOptionPane.showMessageDialog(this, ex.getMensaje(), "Error al registrar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarErrorCodigo() {
        JOptionPane.showMessageDialog(this, "El código de empleado es obligatorio.",
                "Campo incompleto", JOptionPane.WARNING_MESSAGE);
    }

    /** @return {@code true} si el usuario fue registrado exitosamente. */
    public boolean isConfirmado() { return confirmado; }

    private static String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return texto.charAt(0) + texto.substring(1).toLowerCase();
    }
}