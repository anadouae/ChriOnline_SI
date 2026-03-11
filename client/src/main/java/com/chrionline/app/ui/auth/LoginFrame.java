package com.chrionline.app.ui.auth;

import com.chrionline.app.model.User;
import com.chrionline.app.network.ApiService;
import com.chrionline.app.network.TcpApiService;
import com.chrionline.app.ui.client.ClientMainFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Fenêtre Connexion + Inscription (maquettes).
 * Après connexion : ouvre toujours l'espace client (ClientMainFrame).
 * L'admin y verra le bouton "Administration" pour accéder à l'espace admin.
 */
public class LoginFrame extends JFrame {

    private final ApiService tcpApiService;
    private final Runnable onClose;
    private boolean loginSuccess;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField nameField;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    public LoginFrame(TcpApiService tcpApiService) {
        this(tcpApiService, null);
    }

    public LoginFrame(ApiService tcpApiService, Runnable onClose) {
        this.tcpApiService = tcpApiService;
        this.onClose = onClose;
        this.loginSuccess = false;
        setTitle("ChriOnline - Connexion");
        setDefaultCloseOperation(onClose != null ? JFrame.DISPOSE_ON_CLOSE : JFrame.EXIT_ON_CLOSE);
        setSize(420, 480);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(new Color(0xf0f4f8));
        if (onClose != null) {
            addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    if (!loginSuccess) onClose.run();
                }
            });
        }
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);

        JPanel loginPanel = buildLoginPanel();
        JPanel registerPanel = buildRegisterPanel();
        cardPanel.add(loginPanel, "login");
        cardPanel.add(registerPanel, "register");
        add(cardPanel, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout());
        JButton goRegister = new JButton("Créer un compte");
        styleSecondaryButton(goRegister);
        goRegister.addActionListener(e -> cardLayout.show(cardPanel, "register"));
        JButton goLogin = new JButton("Déjà inscrit ? Connexion");
        styleSecondaryButton(goLogin);
        goLogin.addActionListener(e -> cardLayout.show(cardPanel, "login"));
        bottom.add(goRegister);
        bottom.add(goLogin);
        bottom.setOpaque(false);
        add(bottom, BorderLayout.SOUTH);
    }

    private JPanel buildLoginPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xdee2e6)),
            BorderFactory.createEmptyBorder(24, 32, 24, 32)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        gbc.gridy = 0;
        JLabel title = new JLabel("Connexion");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        p.add(title, gbc);
        gbc.gridy = 1;
        p.add(new JLabel(" "), gbc);
        gbc.gridy = 2;
        p.add(new JLabel("Email"), gbc);
        gbc.gridy = 3;
        emailField = new JTextField(24);
        emailField.setPreferredSize(new Dimension(280, 32));
        emailField.putClientProperty("JTextField.placeholderText", "exemple@email.com");
        p.add(emailField, gbc);
        gbc.gridy = 4;
        p.add(new JLabel("Mot de passe"), gbc);
        gbc.gridy = 5;
        passwordField = new JPasswordField(24);
        passwordField.setPreferredSize(new Dimension(280, 32));
        p.add(passwordField, gbc);
        gbc.gridy = 6;
        p.add(new JLabel(" "), gbc);
        gbc.gridy = 7;
        JButton loginBtn = new JButton("Se connecter");
        stylePrimaryButton(loginBtn);
        loginBtn.addActionListener(e -> doLogin());
        p.add(loginBtn, gbc);
        gbc.gridy = 8;
        JLabel link = new JLabel("Créer un compte");
        link.setForeground(new Color(0x1976d2));
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent ev) { cardLayout.show(cardPanel, "register"); }
        });
        p.add(link, gbc);
        gbc.gridy = 9;
        p.add(buildTestAccountsBlock(), gbc);
        return p;
    }

    private JPanel buildTestAccountsBlock() {
        JPanel p = new JPanel(new GridLayout(4, 1));
        p.setBackground(new Color(0xe3f2fd));
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        p.add(new JLabel("Comptes de test:"));
        p.add(new JLabel("Client: jean@example.com"));
        p.add(new JLabel("Admin: admin@chrionline.com"));
        p.add(new JLabel("(mot de passe : n'importe lequel)"));
        return p;
    }

    private JPanel buildRegisterPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xdee2e6)),
            BorderFactory.createEmptyBorder(24, 32, 24, 32)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        gbc.gridy = 0;
        JLabel title = new JLabel("Inscription");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        p.add(title, gbc);
        gbc.gridy = 1;
        p.add(new JLabel("Nom"), gbc);
        gbc.gridy = 2;
        nameField = new JTextField(24);
        nameField.setPreferredSize(new Dimension(280, 32));
        nameField.putClientProperty("JTextField.placeholderText", "Votre nom");
        p.add(nameField, gbc);
        gbc.gridy = 3;
        p.add(new JLabel("Email"), gbc);
        gbc.gridy = 4;
        JTextField regEmail = new JTextField(24);
        regEmail.setPreferredSize(new Dimension(280, 32));
        regEmail.putClientProperty("JTextField.placeholderText", "exemple@email.com");
        p.add(regEmail, gbc);
        gbc.gridy = 5;
        p.add(new JLabel("Mot de passe"), gbc);
        gbc.gridy = 6;
        JPasswordField regPass = new JPasswordField(24);
        regPass.setPreferredSize(new Dimension(280, 32));
        p.add(regPass, gbc);
        gbc.gridy = 7;
        JButton registerBtn = new JButton("S'inscrire");
        stylePrimaryButton(registerBtn);
        registerBtn.addActionListener(e -> {
            doRegister(regEmail, regPass);
        });
        p.add(registerBtn, gbc);
        gbc.gridy = 8;
        JLabel link = new JLabel("Déjà inscrit ? Connexion");
        link.setForeground(new Color(0x1976d2));
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent ev) { cardLayout.show(cardPanel, "login"); }
        });
        p.add(link, gbc);
        return p;
    }

    private void doRegister(JTextField regEmail, JPasswordField regPass) {
        String email = regEmail.getText().trim();
        String pass = new String(regPass.getPassword());
        String name = nameField.getText().trim();
        if (email.isEmpty() || pass.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Remplissez tous les champs.");
            return;
        }
        tcpApiService.register(email, pass, name);
        User u = tcpApiService.getCurrentUser();
        if (u != null) {
            JOptionPane.showMessageDialog(this, "Inscription réussie. Connectez-vous.");
            cardLayout.show(cardPanel, "login");
            emailField.setText(email);
        }
    }

    private void doLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Renseignez l'email.");
            return;
        }
        User user = tcpApiService.login(email, password);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Échec de connexion.");
            return;
        }
        loginSuccess = true;
        dispose();
        ClientMainFrame clientFrame = new ClientMainFrame(tcpApiService);
        clientFrame.setVisible(true);
    }

    private void stylePrimaryButton(JButton b) {
        b.setBackground(new Color(0x1e3a5f));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(200, 36));
    }

    private void styleSecondaryButton(JButton b) {
        b.setBackground(Color.WHITE);
        b.setForeground(new Color(0x1e3a5f));
        b.setFocusPainted(false);
    }
}
