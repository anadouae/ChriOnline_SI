package com.chrionline.app.network;

import com.chrionline.app.model.Product;
import com.chrionline.app.model.User;
import com.chrionline.app.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour la classe TcpApiService.
 * Utilise Mockito pour mocker les connexions socket et les réponses du serveur.
 */
@DisplayName("TcpApiService Network Tests")
@ExtendWith(MockitoExtension.class)
class TcpApiServiceTest {

    private TcpApiService tcpApiService;

    @Mock
    private Socket mockSocket;

    @Mock
    private BufferedReader mockReader;

    @Mock
    private PrintWriter mockWriter;

    @BeforeEach
    void setUp() {
        tcpApiService = new TcpApiService("localhost", 12345);
    }

    @Test
    @DisplayName("Devrait créer un service avec les paramètres par défaut")
    void testTcpApiServiceDefaultConstructor() {
        TcpApiService service = new TcpApiService();
        assertThat(service).isNotNull();
    }

    @Test
    @DisplayName("Devrait créer un service avec les paramètres personnalisés")
    void testTcpApiServiceCustomConstructor() {
        assertThat(tcpApiService).isNotNull();
    }

    @Test
    @DisplayName("Devrait être déconnecté au démarrage")
    void testInitiallyDisconnected() {
        assertThat(tcpApiService.isConnected()).isFalse();
    }

    @Test
    @DisplayName("Devrait déconnecter proprement")
    void testDisconnect() {
        tcpApiService.disconnect();
        assertThat(tcpApiService.isConnected()).isFalse();
    }

    @Test
    @DisplayName("Devrait retourner null pour l'utilisateur courant au démarrage")
    void testGetCurrentUserInitially() {
        User user = tcpApiService.getCurrentUser();
        assertThat(user).isNull();
    }

    /*
    @Test
    @DisplayName("Devrait parser correctement un entier valide")
    void testParseIntValid() {
        int result = TcpApiService.parseInt("123", -1);
        assertThat(result).isEqualTo(123);
    }

    @Test
    @DisplayName("Devrait retourner la valeur par défaut pour un entier invalide")
    void testParseIntInvalid() {
        int result = TcpApiService.parseInt("invalid", -1);
        assertThat(result).isEqualTo(-1);
    }

    @Test
    @DisplayName("Devrait parser zéro correctement")
    void testParseIntZero() {
        int result = TcpApiService.parseInt("0", -1);
        assertThat(result).isEqualTo(0);
    }

    @Test
    @DisplayName("Devrait parser les entiers négatifs")
    void testParseIntNegative() {
        int result = TcpApiService.parseInt("-42", -1);
        assertThat(result).isEqualTo(-42);
    }
    */

    @Test
    @DisplayName("Devrait retourner null quand la connexion échoue")
    void testLoginWithConnectionFailure() {
        // Pas de connexion réelle, devrait retourner null
        User user = tcpApiService.login("test@example.com", "password");
        assertThat(user).isNull();
    }

    @Test
    @DisplayName("Devrait retourner false lors de l'enregistrement sans connexion")
    void testRegisterWithConnectionFailure() {
        boolean result = tcpApiService.register("test@example.com", "password", "Test User");
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Devrait gérer la déconnexion quand pas d'utilisateur")
    void testLogoutWithoutUser() {
        tcpApiService.logout();
        assertThat(tcpApiService.getCurrentUser()).isNull();
    }
}

