package com.chrionline.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitaires pour la classe User.
 */
@DisplayName("User Model Tests")
class UserTest {

    private User clientUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        clientUser = new User(1, "Jean Dupont", "jean@example.com", UserRole.CLIENT);
        adminUser = new User(2, "Alice Admin", "admin@example.com", UserRole.ADMIN);
    }

    @Test
    @DisplayName("Devrait créer un utilisateur CLIENT")
    void testCreateClientUser() {
        assertThat(clientUser).isNotNull();
        assertThat(clientUser.getId()).isEqualTo(1);
        assertThat(clientUser.getName()).isEqualTo("Jean Dupont");
        assertThat(clientUser.getEmail()).isEqualTo("jean@example.com");
        assertThat(clientUser.getRole()).isEqualTo(UserRole.CLIENT);
        assertThat(clientUser.isAdmin()).isFalse();
    }

    @Test
    @DisplayName("Devrait créer un utilisateur ADMIN")
    void testCreateAdminUser() {
        assertThat(adminUser).isNotNull();
        assertThat(adminUser.getId()).isEqualTo(2);
        assertThat(adminUser.getName()).isEqualTo("Alice Admin");
        assertThat(adminUser.getEmail()).isEqualTo("admin@example.com");
        assertThat(adminUser.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(adminUser.isAdmin()).isTrue();
    }

    @Test
    @DisplayName("Devrait retourner false pour isAdmin() sur un client")
    void testIsAdminFalseForClient() {
        assertThat(clientUser.isAdmin()).isFalse();
    }

    @Test
    @DisplayName("Devrait retourner true pour isAdmin() sur un admin")
    void testIsAdminTrueForAdmin() {
        assertThat(adminUser.isAdmin()).isTrue();
    }

    @Test
    @DisplayName("Devrait retourner l'ID de l'utilisateur")
    void testGetId() {
        assertThat(clientUser.getId()).isEqualTo(1);
        assertThat(adminUser.getId()).isEqualTo(2);
    }

    @Test
    @DisplayName("Devrait retourner le nom de l'utilisateur")
    void testGetName() {
        assertThat(clientUser.getName()).isEqualTo("Jean Dupont");
        assertThat(adminUser.getName()).isEqualTo("Alice Admin");
    }

    @Test
    @DisplayName("Devrait retourner l'email de l'utilisateur")
    void testGetEmail() {
        assertThat(clientUser.getEmail()).isEqualTo("jean@example.com");
        assertThat(adminUser.getEmail()).isEqualTo("admin@example.com");
    }

    @Test
    @DisplayName("Devrait retourner le rôle de l'utilisateur")
    void testGetRole() {
        assertThat(clientUser.getRole()).isEqualTo(UserRole.CLIENT);
        assertThat(adminUser.getRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    @DisplayName("Devrait accepter des noms avec espaces et caractères spéciaux")
    void testNameWithSpecialCharacters() {
        User user = new User(3, "Jean-Pierre O'Brien", "jean-pierre@example.com", UserRole.CLIENT);
        assertThat(user.getName()).isEqualTo("Jean-Pierre O'Brien");
    }

    @Test
    @DisplayName("Devrait accepter des emails valides")
    void testValidEmail() {
        User user = new User(4, "Test", "test.user+tag@example.co.uk", UserRole.CLIENT);
        assertThat(user.getEmail()).isEqualTo("test.user+tag@example.co.uk");
    }
}

