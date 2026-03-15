package com.chrionline.common.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitaires pour la classe Product (modèle commun).
 */
@DisplayName("Common Product Model Tests")
class ProductTest {

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product(1, "Laptop", "High-performance laptop", 999.99, 50);
    }

    @Test
    @DisplayName("Devrait créer un produit avec un constructeur vide")
    void testEmptyConstructor() {
        Product emptyProduct = new Product();
        assertThat(emptyProduct).isNotNull();
    }

    @Test
    @DisplayName("Devrait créer un produit avec les paramètres")
    void testConstructorWithParameters() {
        assertThat(product).isNotNull();
        assertThat(product.getId()).isEqualTo(1);
        assertThat(product.getName()).isEqualTo("Laptop");
        assertThat(product.getDescription()).isEqualTo("High-performance laptop");
        assertThat(product.getPrice()).isEqualTo(999.99);
        assertThat(product.getStock()).isEqualTo(50);
    }

    @Test
    @DisplayName("Devrait modifier l'ID du produit")
    void testSetId() {
        product.setId(42);
        assertThat(product.getId()).isEqualTo(42);
    }

    @Test
    @DisplayName("Devrait modifier le nom du produit")
    void testSetName() {
        product.setName("Desktop");
        assertThat(product.getName()).isEqualTo("Desktop");
    }

    @Test
    @DisplayName("Devrait modifier la description du produit")
    void testSetDescription() {
        product.setDescription("Powerful desktop computer");
        assertThat(product.getDescription()).isEqualTo("Powerful desktop computer");
    }

    @Test
    @DisplayName("Devrait modifier le prix du produit")
    void testSetPrice() {
        product.setPrice(1299.99);
        assertThat(product.getPrice()).isEqualTo(1299.99);
    }

    @Test
    @DisplayName("Devrait modifier le stock du produit")
    void testSetStock() {
        product.setStock(75);
        assertThat(product.getStock()).isEqualTo(75);
    }

    @Test
    @DisplayName("Devrait retourner l'ID")
    void testGetId() {
        assertThat(product.getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("Devrait retourner le nom")
    void testGetName() {
        assertThat(product.getName()).isEqualTo("Laptop");
    }

    @Test
    @DisplayName("Devrait retourner la description")
    void testGetDescription() {
        assertThat(product.getDescription()).isEqualTo("High-performance laptop");
    }

    @Test
    @DisplayName("Devrait retourner le prix")
    void testGetPrice() {
        assertThat(product.getPrice()).isEqualTo(999.99);
    }

    @Test
    @DisplayName("Devrait retourner le stock")
    void testGetStock() {
        assertThat(product.getStock()).isEqualTo(50);
    }

    @Test
    @DisplayName("Devrait supporter les modificateurs multiples")
    void testMultipleSetters() {
        product.setId(999);
        product.setName("Updated Product");
        product.setDescription("Updated Description");
        product.setPrice(1500.0);
        product.setStock(100);

        assertThat(product.getId()).isEqualTo(999);
        assertThat(product.getName()).isEqualTo("Updated Product");
        assertThat(product.getDescription()).isEqualTo("Updated Description");
        assertThat(product.getPrice()).isEqualTo(1500.0);
        assertThat(product.getStock()).isEqualTo(100);
    }

    @Test
    @DisplayName("Devrait supporter les stocks zéro")
    void testZeroStock() {
        product.setStock(0);
        assertThat(product.getStock()).isEqualTo(0);
    }

    @Test
    @DisplayName("Devrait supporter les stocks négatifs")
    void testNegativeStock() {
        product.setStock(-10);
        assertThat(product.getStock()).isEqualTo(-10);
    }

    @Test
    @DisplayName("Devrait supporter les prix décimaux")
    void testDecimalPrice() {
        product.setPrice(99.99);
        assertThat(product.getPrice()).isEqualTo(99.99);
    }

    @Test
    @DisplayName("Devrait être sérialisable")
    void testSerializable() {
        assertThat(product).isInstanceOf(java.io.Serializable.class);
    }

    @Test
    @DisplayName("Devrait avoir un serialVersionUID")
    void testSerialVersionUID() {
        assertThat(Product.class.getDeclaredFields()).anyMatch(f -> f.getName().equals("serialVersionUID"));
    }
}

