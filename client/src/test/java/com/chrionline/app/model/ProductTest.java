package com.chrionline.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitaires pour la classe Product.
 */
@DisplayName("Product Model Tests")
class ProductTest {

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product(1, "Ordinateur portable", "PC portable 15 pouces, 8 Go RAM", 599.99, 50, "Informatique");
    }

    @Test
    @DisplayName("Devrait créer un produit avec tous les paramètres")
    void testProductCreation() {
        assertThat(product).isNotNull();
        assertThat(product.getId()).isEqualTo(1);
        assertThat(product.getName()).isEqualTo("Ordinateur portable");
        assertThat(product.getDescription()).isEqualTo("PC portable 15 pouces, 8 Go RAM");
        assertThat(product.getPrice()).isEqualTo(599.99);
        assertThat(product.getStock()).isEqualTo(50);
        assertThat(product.getCategory()).isEqualTo("Informatique");
    }

    @Test
    @DisplayName("Devrait gérer un produit sans catégorie")
    void testProductWithoutCategory() {
        Product productNoCategory = new Product(2, "Souris", "Souris sans fil", 25.99, 100, null);
        assertThat(productNoCategory.getCategory()).isEmpty();
    }

    @Test
    @DisplayName("Devrait mettre à jour le stock")
    void testSetStock() {
        product.setStock(75);
        assertThat(product.getStock()).isEqualTo(75);
    }

    @Test
    @DisplayName("Devrait mettre à jour le nom")
    void testSetName() {
        product.setName("Laptop Pro");
        assertThat(product.getName()).isEqualTo("Laptop Pro");
    }

    @Test
    @DisplayName("Devrait mettre à jour la description")
    void testSetDescription() {
        product.setDescription("Nouveau PC haute performance");
        assertThat(product.getDescription()).isEqualTo("Nouveau PC haute performance");
    }

    @Test
    @DisplayName("Devrait mettre à jour le prix")
    void testSetPrice() {
        product.setPrice(699.99);
        assertThat(product.getPrice()).isEqualTo(699.99);
    }

    @Test
    @DisplayName("Devrait mettre à jour la catégorie")
    void testSetCategory() {
        product.setCategory("Électronique");
        assertThat(product.getCategory()).isEqualTo("Électronique");
    }

    @Test
    @DisplayName("Devrait accepter un stock négatif (pas de validation)")
    void testNegativeStock() {
        product.setStock(-10);
        assertThat(product.getStock()).isEqualTo(-10);
    }

    @Test
    @DisplayName("Devrait accepter un prix zéro")
    void testZeroPrice() {
        product.setPrice(0);
        assertThat(product.getPrice()).isEqualTo(0);
    }

    @Test
    @DisplayName("Devrait accepter des prix décimaux")
    void testDecimalPrice() {
        product.setPrice(99.99);
        assertThat(product.getPrice()).isEqualTo(99.99);
    }
}

