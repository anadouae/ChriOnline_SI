package com.chrionline.server.service;

import com.chrionline.common.Protocol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitaires pour la classe ProductService.
 * Tests de validation et de parsing sans base de données réelle.
 */
@DisplayName("ProductService Server Tests")
class ProductServiceTest {

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService();
    }

    @Test
    @DisplayName("Devrait créer un service produits")
    void testProductServiceCreation() {
        assertThat(productService).isNotNull();
    }

    @Test
    @DisplayName("La réponse devrait commencer par OK ou ERROR")
    void testResponseFormat() {
        String response = productService.getProducts();
        assertThat(response).isNotNull();
        assertThat(response).satisfiesAnyOf(
                res -> assertThat(res).startsWith(Protocol.OK),
                res -> assertThat(res).startsWith(Protocol.ERROR)
        );
    }

    @Test
    @DisplayName("getProduct avec ID invalide devrait retourner ERROR")
    void testGetProductWithInvalidId() {
        String response = productService.getProduct("invalid");
        assertThat(response).startsWith(Protocol.ERROR);
        assertThat(response).isEqualTo(Protocol.ERROR + Protocol.SEPARATOR + "INVALID_DATA");
    }

    @Test
    @DisplayName("getProduct avec ID vide devrait retourner ERROR")
    void testGetProductWithEmptyId() {
        String response = productService.getProduct("");
        assertThat(response)
                .startsWith(Protocol.ERROR)
                .isEqualTo(Protocol.ERROR + Protocol.SEPARATOR + "INVALID_DATA");
    }

    @Test
    @DisplayName("getProduct avec ID null devrait retourner ERROR|INVALID_DATA")
    void testGetProductWithNullId() {
        String response = productService.getProduct(null);
        assertThat(response)
                .startsWith(Protocol.ERROR)
                .isEqualTo(Protocol.ERROR + Protocol.SEPARATOR + "INVALID_DATA");
    }

    /*@Test
    @DisplayName("getStock devrait retourner un entier")
    void testGetStock() {
        int stock = productService.getStock(1);
        // stock peut-être -1 si produit non trouvé, c'est normal
        assertThat(stock).isGreaterThanOrEqualTo(-1);
    }*/

    @Test
    @DisplayName("getStock avec ID invalide devrait retourner -1")
    void testGetStockInvalidId() {
        int stock = productService.getStock(-999);
        assertThat(stock).isEqualTo(-1);
    }
}

