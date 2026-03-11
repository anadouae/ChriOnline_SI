package com.chrionline.app.ui.home;

import com.chrionline.app.network.TcpApiService;
import com.chrionline.app.ui.auth.LoginFrame;
import com.chrionline.app.ui.components.UiConstants;

import javax.swing.*;
import java.awt.*;

/**
 * Homepage (landing) affichée en premier : hero, "Se connecter", "Créer un compte", sections.
 */
public class HomeFrame extends JFrame {

    private final TcpApiService tcpApiService;

    public HomeFrame(TcpApiService tcpApiService) {
        this.tcpApiService = tcpApiService;
        setTitle("ChriOnline - Votre boutique en ligne");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 750);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        JLabel logo = new JLabel("ChriOnline");
        logo.setFont(logo.getFont().deriveFont(Font.BOLD, 20f));
        logo.setForeground(UiConstants.BLUE_DARK);
        header.add(logo, BorderLayout.WEST);
        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        headerRight.setOpaque(false);
        JButton loginLink = new JButton("Se connecter");
        loginLink.setBorderPainted(false);
        loginLink.setContentAreaFilled(false);
        loginLink.setForeground(UiConstants.BLUE_DARK);
        loginLink.addActionListener(e -> openLogin());
        JButton createAccount = new JButton("Créer un compte");
        createAccount.setBackground(UiConstants.BLUE_DARK);
        createAccount.setForeground(Color.BLACK);
        createAccount.setFocusPainted(false);
        createAccount.addActionListener(e -> openRegister());
        headerRight.add(loginLink);
        headerRight.add(createAccount);
        header.add(headerRight, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(Color.WHITE);
        center.setBorder(BorderFactory.createEmptyBorder(24, 48, 24, 48));

        JLabel heroTitle = new JLabel("Votre boutique en ligne de confiance");
        heroTitle.setFont(heroTitle.getFont().deriveFont(Font.BOLD, 22f));
        heroTitle.setForeground(UiConstants.BLUE_DARK);
        heroTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(heroTitle);
        center.add(Box.createVerticalStrut(12));
        JLabel heroDesc = new JLabel("<html>Découvrez des milliers de produits de qualité, livrés rapidement chez vous.<br>ChriOnline vous offre une expérience d'achat simple et sécurisée.</html>");
        heroDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(heroDesc);
        center.add(Box.createVerticalStrut(16));
        JPanel heroBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        heroBtns.setOpaque(false);
        JButton startBtn = new JButton("Commencer maintenant");
        startBtn.setBackground(UiConstants.BLUE_DARK);
        startBtn.setForeground(UiConstants.GRAY_DARK);
        startBtn.setFocusPainted(false);
        startBtn.addActionListener(e -> openLogin());
        JButton loginBtn2 = new JButton("Se connecter");
        loginBtn2.setBorder(BorderFactory.createLineBorder(UiConstants.BLUE_DARK));
        loginBtn2.setBackground(Color.WHITE);
        loginBtn2.addActionListener(e -> openLogin());
        heroBtns.add(startBtn);
        heroBtns.add(loginBtn2);
        center.add(heroBtns);
        center.add(Box.createVerticalStrut(32));

        JLabel whyTitle = new JLabel("Pourquoi choisir ChriOnline ?");
        whyTitle.setFont(whyTitle.getFont().deriveFont(Font.BOLD, 16f));
        whyTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(whyTitle);
        center.add(Box.createVerticalStrut(12));
        JPanel whyCards = new JPanel(new GridLayout(1, 4, 12, 0));
        whyCards.setOpaque(false);
        whyCards.add(buildWhyCard("Large catalogue", "Des milliers de produits disponibles dans toutes les catégories"));
        whyCards.add(buildWhyCard("Livraison rapide", "Recevez vos commandes en 24-48h partout en France"));
        whyCards.add(buildWhyCard("Paiement sécurisé", "Vos transactions sont protégées par les dernières technologies"));
        whyCards.add(buildWhyCard("Support 24/7", "Notre équipe est disponible pour vous aider à tout moment"));
        center.add(whyCards);
        center.add(Box.createVerticalStrut(24));

        JPanel ctaPanel = new JPanel(new BorderLayout(8, 8));
        ctaPanel.setBackground(UiConstants.BLUE_DARK);
        ctaPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel ctaTitle = new JLabel("Prêt à commencer ?");
        ctaTitle.setForeground(Color.WHITE);
        ctaTitle.setFont(ctaTitle.getFont().deriveFont(Font.BOLD, 16f));
        ctaPanel.add(ctaTitle, BorderLayout.NORTH);
        JLabel ctaDesc = new JLabel("Créez votre compte gratuitement et découvrez tous nos produits.");
        ctaDesc.setForeground(Color.WHITE);
        ctaPanel.add(ctaDesc, BorderLayout.CENTER);
        JButton ctaBtn = new JButton("Créer un compte gratuit");
        ctaBtn.setBackground(Color.WHITE);
        ctaBtn.setForeground(UiConstants.BLUE_DARK);
        ctaBtn.setFocusPainted(false);
        ctaBtn.addActionListener(e -> openLogin());
        ctaPanel.add(ctaBtn, BorderLayout.SOUTH);
        center.add(ctaPanel);
        center.add(Box.createVerticalStrut(24));

        JLabel howTitle = new JLabel("Comment ça marche ?");
        howTitle.setFont(howTitle.getFont().deriveFont(Font.BOLD, 16f));
        howTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(howTitle);
        center.add(Box.createVerticalStrut(12));
        JPanel steps = new JPanel(new GridLayout(1, 3, 24, 0));
        steps.setOpaque(false);
        steps.add(buildStep("1", "Créez votre compte", "Inscription rapide et gratuite en quelques secondes"));
        steps.add(buildStep("2", "Parcourez le catalogue", "Explorez nos produits et ajoutez-les à votre panier"));
        steps.add(buildStep("3", "Recevez chez vous", "Passez commande et recevez vos produits rapidement"));
        center.add(steps);

        add(new JScrollPane(center), BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(0xf5f5f5));
        footer.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        footer.add(new JLabel("© 2024 ChriOnline. Tous droits réservés."), BorderLayout.WEST);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel buildWhyCard(String title, String text) {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xe0e0e0)),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        p.add(new JLabel(title, SwingConstants.CENTER), BorderLayout.NORTH);
        p.add(new JLabel("<html><center>" + text + "</center></html>"), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildStep(String num, String title, String text) {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setOpaque(false);
        JLabel numLabel = new JLabel(num, SwingConstants.CENTER);
        numLabel.setFont(numLabel.getFont().deriveFont(Font.BOLD, 18f));
        numLabel.setOpaque(true);
        numLabel.setBackground(UiConstants.BLUE_DARK);
        numLabel.setForeground(Color.WHITE);
        numLabel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        p.add(numLabel, BorderLayout.NORTH);
        p.add(new JLabel("<html><b>" + title + "</b><br>" + text + "</html>"), BorderLayout.CENTER);
        return p;
    }

    private void openLogin() {
        setVisible(false);
        LoginFrame login = new LoginFrame(tcpApiService, () -> setVisible(true));
        login.showLoginTab();
        login.setVisible(true);
    }

    private void openRegister() {
        setVisible(false);
        LoginFrame login = new LoginFrame(tcpApiService, () -> setVisible(true));
        login.showRegisterTab();
        login.setVisible(true);
    }
}
