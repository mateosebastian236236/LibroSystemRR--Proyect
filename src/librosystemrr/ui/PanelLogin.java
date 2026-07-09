package librosystemrr.ui;

import librosystemrr.modelos.Usuario;
import librosystemrr.sistema.SistemaBiblioteca;
import librosystemrr.tads.ListaEnlazada;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Panel de login que se muestra antes de acceder al sistema.
 * Autentica al usuario verificando ID y contraseña contra la lista de usuarios registrados.
 *
 * <p>También acepta la cuenta de administrador (admin / admin123) como acceso maestro.</p>
 */
@SuppressWarnings({"serial", "this-escape"})
public class PanelLogin extends JDialog {

    private final SistemaBiblioteca sistema;

    private JTextField campoId;
    private JPasswordField campoContrasena;
    private JLabel lblError;

    private Usuario usuarioAutenticado;
    private boolean loginExitoso = false;

    private static final String ADMIN_ID  = "admin";
    private static final String ADMIN_PASS = "admin123";

    /**
     * Construye el diálogo de login.
     *
     * @param sistema Sistema de biblioteca para verificar credenciales.
     */
    public PanelLogin(SistemaBiblioteca sistema) {
        super((JFrame) null, "LibroSystemRR — Iniciar sesión", true);
        this.sistema = sistema;
        inicializarUI();
        pack();
        setSize(420, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { System.exit(0); }
        });
    }

    private void inicializarUI() {
        setLayout(new BorderLayout());

        // ── Encabezado ──
        JPanel encabezado = new JPanel(new GridLayout(2, 1));
        encabezado.setBackground(new Color(30, 58, 95));
        encabezado.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titulo = new JLabel("LibroSystemRR", JLabel.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel("Sistema de Gestión de Biblioteca — EPN", JLabel.CENTER);
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitulo.setForeground(new Color(180, 210, 240));

        encabezado.add(titulo);
        encabezado.add(subtitulo);
        add(encabezado, BorderLayout.NORTH);

        // ── Formulario ──
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createEmptyBorder(25, 40, 10, 40));
        formulario.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formulario.add(new JLabel("Usuario (ID):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        campoId = new JTextField(15);
        campoId.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formulario.add(campoId, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formulario.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        campoContrasena = new JPasswordField(15);
        campoContrasena.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formulario.add(campoContrasena, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        lblError = new JLabel(" ", JLabel.CENTER);
        lblError.setForeground(new Color(180, 30, 30));
        lblError.setFont(new Font("SansSerif", Font.ITALIC, 11));
        formulario.add(lblError, gbc);

        add(formulario, BorderLayout.CENTER);

        // ── Botón de ingreso ──
        JPanel panelBoton = new JPanel(new BorderLayout());
        panelBoton.setBackground(Color.WHITE);
        panelBoton.setBorder(BorderFactory.createEmptyBorder(0, 40, 20, 40));

        JButton btnIngresar = new JButton("Ingresar al sistema");
        btnIngresar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnIngresar.setBackground(new Color(30, 58, 95));
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setFocusPainted(false);
        btnIngresar.setOpaque(true);
        btnIngresar.setBorderPainted(false);
        btnIngresar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnIngresar.setPreferredSize(new Dimension(0, 38));

        btnIngresar.addActionListener(e -> autenticar());
        campoContrasena.addActionListener(e -> autenticar());
        campoId.addActionListener(e -> campoContrasena.requestFocus());

        panelBoton.add(btnIngresar, BorderLayout.CENTER);
        add(panelBoton, BorderLayout.SOUTH);
    }

    /**
     * Verifica las credenciales ingresadas contra la lista de usuarios
     * y contra la cuenta de administrador.
     */
    private void autenticar() {
        String id = campoId.getText().trim();
        String pass = new String(campoContrasena.getPassword()).trim();

        if (id.isEmpty() || pass.isEmpty()) {
            lblError.setText("Ingresa tu ID y contraseña.");
            return;
        }

        // Verificar cuenta de administrador
        if (id.equals(ADMIN_ID) && pass.equals(ADMIN_PASS)) {
            loginExitoso = true;
            usuarioAutenticado = null; // admin no es un Usuario del sistema
            dispose();
            return;
        }

        // Verificar contra usuarios registrados
        ListaEnlazada<Usuario> usuarios = sistema.getUsuarios();
        for (int i = 0; i < usuarios.getTamanio(); i++) {
            Usuario u = usuarios.obtener(i);
            if (u.getId().equals(id) && u.getContrasena().equals(pass)) {
                loginExitoso = true;
                usuarioAutenticado = u;
                dispose();
                return;
            }
        }

        lblError.setText("ID o contraseña incorrectos. Intenta de nuevo.");
        campoContrasena.setText("");
        campoContrasena.requestFocus();
    }

    /**
     * Indica si el login fue exitoso.
     *
     * @return {@code true} si las credenciales fueron válidas.
     */
    public boolean isLoginExitoso() { return loginExitoso; }

    /**
     * Retorna el usuario autenticado.
     *
     * @return El {@link Usuario} que inició sesión, o {@code null} si es el admin.
     */
    public Usuario getUsuarioAutenticado() { return usuarioAutenticado; }
}