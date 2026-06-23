package librosystemrr.ui.dialogos;

import librosystemrr.excepciones.LibroSystemException;
import librosystemrr.modelos.Prestamo;
import librosystemrr.sistema.SistemaBiblioteca;

import javax.swing.*;
import java.awt.*;

/**
 * Diálogo modal para registrar un nuevo préstamo en el sistema.
 * Solicita el ID del usuario y el ISBN del libro.
 */
public class DialogoPrestamo extends JDialog {

    private final SistemaBiblioteca sistema;

    private JTextField campoIdUsuario;
    private JTextField campoIsbn;
    private JLabel lblFeedback;

    private boolean confirmado = false;

    /**
     * @param padre   Ventana padre.
     * @param sistema Sistema de biblioteca.
     */
    public DialogoPrestamo(JFrame padre, SistemaBiblioteca sistema) {
        super(padre, "Registrar nuevo préstamo", true);
        this.sistema = sistema;
        inicializarUI();
        pack();
        setMinimumSize(new Dimension(400, 200));
        setLocationRelativeTo(padre);
    }

    private void inicializarUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formulario.add(new JLabel("ID del usuario:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        campoIdUsuario = new JTextField(18);
        formulario.add(campoIdUsuario, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formulario.add(new JLabel("ISBN del libro:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        campoIsbn = new JTextField(18);
        formulario.add(campoIsbn, gbc);

        // Feedback de validación en tiempo real
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        lblFeedback = new JLabel(" ");
        lblFeedback.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblFeedback.setForeground(new Color(160, 40, 20));
        formulario.add(lblFeedback, gbc);

        add(formulario, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnRegistrar = new JButton("Registrar préstamo");
        JButton btnCancelar  = new JButton("Cancelar");

        btnRegistrar.setBackground(new Color(30, 58, 95));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFocusPainted(false);

        btnRegistrar.addActionListener(e -> registrar());
        btnCancelar.addActionListener(e -> dispose());

        botones.add(btnCancelar);
        botones.add(btnRegistrar);
        add(botones, BorderLayout.SOUTH);
    }

    private void registrar() {
        String idUsuario = campoIdUsuario.getText().trim();
        String isbn = campoIsbn.getText().trim();

        if (idUsuario.isEmpty() || isbn.isEmpty()) {
            lblFeedback.setText("El ID del usuario y el ISBN son obligatorios.");
            return;
        }

        try {
            Prestamo p = sistema.registrarPrestamo(idUsuario, isbn);
            confirmado = true;
            JOptionPane.showMessageDialog(this,
                    "Préstamo registrado exitosamente.\n" +
                            "ID: " + p.getId() + "\n" +
                            "Fecha devolución: " + new java.text.SimpleDateFormat("dd/MM/yyyy")
                            .format(p.getFechaDevolucion()),
                    "Préstamo creado", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (LibroSystemException ex) {
            lblFeedback.setText(ex.getMensaje());
        }
    }

    /** @return {@code true} si el préstamo fue registrado exitosamente. */
    public boolean isConfirmado() { return confirmado; }
}