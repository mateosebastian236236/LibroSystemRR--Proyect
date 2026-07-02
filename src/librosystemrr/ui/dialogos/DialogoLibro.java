package librosystemrr.ui.dialogos;

import librosystemrr.excepciones.LibroSystemException;
import librosystemrr.modelos.Libro;
import librosystemrr.sistema.SistemaBiblioteca;

import javax.swing.*;
import java.awt.*;

/**
 * Diálogo modal para registrar un nuevo libro en el catálogo.
 */
@SuppressWarnings({"serial", "this-escape"})
public class DialogoLibro extends JDialog {

    private final SistemaBiblioteca sistema;

    private JTextField campoIsbn;
    private JTextField campoTitulo;
    private JTextField campoAutor;
    private JTextField campoAnio;

    private boolean confirmado = false;

    /**
     * @param padre   Ventana padre.
     * @param sistema Sistema de biblioteca.
     */
    public DialogoLibro(JFrame padre, SistemaBiblioteca sistema) {
        super(padre, "Registrar nuevo libro", true);
        this.sistema = sistema;
        inicializarUI();
        pack();
        setMinimumSize(new Dimension(400, 220));
        setLocationRelativeTo(padre);
    }

    private void inicializarUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        String[] etiquetas = {"ISBN:", "Título:", "Autor:", "Año de publicación:"};
        JTextField[] campos = new JTextField[4];
        for (int i = 0; i < etiquetas.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formulario.add(new JLabel(etiquetas[i]), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            campos[i] = new JTextField(18);
            formulario.add(campos[i], gbc);
        }
        campoIsbn   = campos[0];
        campoTitulo = campos[1];
        campoAutor  = campos[2];
        campoAnio   = campos[3];

        add(formulario, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnGuardar  = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.setBackground(new Color(30, 58, 95));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);

        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose());

        botones.add(btnCancelar);
        botones.add(btnGuardar);
        add(botones, BorderLayout.SOUTH);
    }

    private void guardar() {
        String isbn   = campoIsbn.getText().trim();
        String titulo = campoTitulo.getText().trim();
        String autor  = campoAutor.getText().trim();
        String anioStr = campoAnio.getText().trim();

        if (isbn.isEmpty() || titulo.isEmpty() || autor.isEmpty() || anioStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.",
                    "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int anio;
        try {
            anio = Integer.parseInt(anioStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El año debe ser un número entero.",
                    "Año inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            sistema.registrarLibro(new Libro(isbn, titulo, autor, anio));
            confirmado = true;
            JOptionPane.showMessageDialog(this, "Libro registrado exitosamente.",
                    "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (LibroSystemException ex) {
            JOptionPane.showMessageDialog(this, ex.getMensaje(),
                    "Error al registrar", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** @return {@code true} si el libro fue registrado exitosamente. */
    public boolean isConfirmado() { return confirmado; }
}