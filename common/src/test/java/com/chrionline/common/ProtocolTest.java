package com.chrionline.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitaires pour la classe Protocol.
 * Vérifie que toutes les constantes sont bien définies et n'ont pas de valeurs nulles.
 */
@DisplayName("Protocol Constants Tests")
class ProtocolTest {

    @Test
    @DisplayName("Devrait avoir la commande REGISTER")
    void testRegisterCommand() {
        assertThat(Protocol.REGISTER).isEqualTo("REGISTER");
    }

    @Test
    @DisplayName("Devrait avoir la commande LOGIN")
    void testLoginCommand() {
        assertThat(Protocol.LOGIN).isEqualTo("LOGIN");
    }

    @Test
    @DisplayName("Devrait avoir la commande LOGOUT")
    void testLogoutCommand() {
        assertThat(Protocol.LOGOUT).isEqualTo("LOGOUT");
    }

    @Test
    @DisplayName("Devrait avoir la commande GET_PRODUCTS")
    void testGetProductsCommand() {
        assertThat(Protocol.GET_PRODUCTS).isEqualTo("GET_PRODUCTS");
    }

    @Test
    @DisplayName("Devrait avoir la commande GET_PRODUCT")
    void testGetProductCommand() {
        assertThat(Protocol.GET_PRODUCT).isEqualTo("GET_PRODUCT");
    }

    @Test
    @DisplayName("Devrait avoir la commande ADD_TO_CART")
    void testAddToCartCommand() {
        assertThat(Protocol.ADD_TO_CART).isEqualTo("ADD_TO_CART");
    }

    @Test
    @DisplayName("Devrait avoir la commande REMOVE_FROM_CART")
    void testRemoveFromCartCommand() {
        assertThat(Protocol.REMOVE_FROM_CART).isEqualTo("REMOVE_FROM_CART");
    }

    @Test
    @DisplayName("Devrait avoir la commande GET_CART")
    void testGetCartCommand() {
        assertThat(Protocol.GET_CART).isEqualTo("GET_CART");
    }

    @Test
    @DisplayName("Devrait avoir la commande VALIDATE_ORDER")
    void testValidateOrderCommand() {
        assertThat(Protocol.VALIDATE_ORDER).isEqualTo("VALIDATE_ORDER");
    }

    @Test
    @DisplayName("Devrait avoir la commande GET_ORDERS")
    void testGetOrdersCommand() {
        assertThat(Protocol.GET_ORDERS).isEqualTo("GET_ORDERS");
    }

    @Test
    @DisplayName("Devrait avoir la réponse OK")
    void testOkResponse() {
        assertThat(Protocol.OK).isEqualTo("OK");
    }

    @Test
    @DisplayName("Devrait avoir la réponse ERROR")
    void testErrorResponse() {
        assertThat(Protocol.ERROR).isEqualTo("ERROR");
    }

    @Test
    @DisplayName("Devrait avoir le séparateur |")
    void testSeparator() {
        assertThat(Protocol.SEPARATOR).isEqualTo("|");
    }

    @Test
    @DisplayName("Devrait avoir le port par défaut 12345")
    void testDefaultPort() {
        assertThat(Protocol.DEFAULT_PORT).isEqualTo(12345);
    }

    @Test
    @DisplayName("Devrait avoir l'hôte par défaut localhost")
    void testDefaultHost() {
        assertThat(Protocol.DEFAULT_HOST).isEqualTo("localhost");
    }

    @Test
    @DisplayName("Toutes les constantes de commande devraient être non-nulles")
    void testAllCommandsNotNull() {
        assertThat(Protocol.REGISTER).isNotNull();
        assertThat(Protocol.LOGIN).isNotNull();
        assertThat(Protocol.LOGOUT).isNotNull();
        assertThat(Protocol.GET_PRODUCTS).isNotNull();
        assertThat(Protocol.GET_PRODUCT).isNotNull();
        assertThat(Protocol.ADD_TO_CART).isNotNull();
        assertThat(Protocol.REMOVE_FROM_CART).isNotNull();
        assertThat(Protocol.GET_CART).isNotNull();
        assertThat(Protocol.VALIDATE_ORDER).isNotNull();
        assertThat(Protocol.GET_ORDERS).isNotNull();
    }

    @Test
    @DisplayName("Toutes les constantes de réponse devraient être non-nulles")
    void testAllResponsesNotNull() {
        assertThat(Protocol.OK).isNotNull();
        assertThat(Protocol.ERROR).isNotNull();
    }

    @Test
    @DisplayName("Toutes les constantes réseau devraient être non-nulles")
    void testAllNetworkConstantsNotNull() {
        assertThat(Protocol.SEPARATOR).isNotNull();
        assertThat(Protocol.DEFAULT_HOST).isNotNull();
    }

    @Test
    @DisplayName("Devrait avoir un port valide (> 0)")
    void testPortValid() {
        assertThat(Protocol.DEFAULT_PORT).isPositive();
    }

    @Test
    @DisplayName("Devrait avoir un hôte valide (non vide)")
    void testHostNotEmpty() {
        assertThat(Protocol.DEFAULT_HOST).isNotEmpty();
    }

    @Test
    @DisplayName("Le séparateur doit avoir une longueur de 1")
    void testSeparatorLength() {
        assertThat(Protocol.SEPARATOR).hasSize(1);
    }
}

