package com.chrionline.common.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitaires pour la classe User (modèle commun).
 */
@DisplayName("Common User Model Tests")
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1, "john@example.com", "John Doe");
    }

    @Test
    @DisplayName("Devrait créer un utilisateur avec un constructeur vide")
    void testEmptyConstructor() {
        User emptyUser = new User();
        assertThat(emptyUser).isNotNull();
    }

    @Test
    @DisplayName("Devrait créer un utilisateur avec les paramètres")
    void testConstructorWithParameters() {
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1);
        assertThat(user.getEmail()).isEqualTo("john@example.com");
        assertThat(user.getName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Devrait modifier l'ID de l'utilisateur")
    void testSetId() {
        user.setId(42);
        assertThat(user.getId()).isEqualTo(42);
    }

    @Test
    @DisplayName("Devrait modifier l'email de l'utilisateur")
    void testSetEmail() {
        user.setEmail("newemail@example.com");
        assertThat(user.getEmail()).isEqualTo("newemail@example.com");
    }

    @Test
    @DisplayName("Devrait modifier le nom de l'utilisateur")
    void testSetName() {
        user.setName("Jane Doe");
        assertThat(user.getName()).isEqualTo("Jane Doe");
    }

    @Test
    @DisplayName("Devrait retourner l'ID")
    void testGetId() {
        assertThat(user.getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("Devrait retourner l'email")
    void testGetEmail() {
        assertThat(user.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    @DisplayName("Devrait retourner le nom")
    void testGetName() {
        assertThat(user.getName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Devrait supporter les modificateurs multiples")
    void testMultipleSetters() {
        user.setId(999);
        user.setEmail("updated@example.com");
        user.setName("Updated Name");

        assertThat(user.getId()).isEqualTo(999);
        assertThat(user.getEmail()).isEqualTo("updated@example.com");
        assertThat(user.getName()).isEqualTo("Updated Name");
    }

    @Test
    @DisplayName("Devrait être sérialisable")
    void testSerializable() {
        assertThat(user).isInstanceOf(java.io.Serializable.class);
    }

    @Test
    @DisplayName("Devrait avoir un serialVersionUID")
    void testSerialVersionUID() {
        assertThat(User.class.getDeclaredFields()).anyMatch(f -> f.getName().equals("serialVersionUID"));
    }
}

