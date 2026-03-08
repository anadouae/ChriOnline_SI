package com.chrionline.app.network;

import com.chrionline.app.model.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service d'accès aux données (mock pour le frontend sans serveur).
 * À remplacer par des appels TCP réels quand le backend est branché.
 */
public class ApiService {

    private User currentUser;
    private final List<Product> products = new ArrayList<>();
    private final List<CartItem> cart = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();
    private final List<User> users = new ArrayList<>();

    public ApiService() {
        loadMockData();
    }

    private void loadMockData() {
        products.add(new Product(1, "MacBook Pro 14\"", "Ordinateur portable haute performance avec puce M3 Pro", 2499.99, 15, "Informatique"));
        products.add(new Product(2, "iPhone 15 Pro", "Smartphone premium avec puce A17 Pro et caméra 48MP", 1199.99, 25, "Téléphonie"));
        products.add(new Product(3, "AirPods Pro", "Écouteurs sans fil avec réduction de bruit active", 279.99, 50, "Audio"));
        products.add(new Product(4, "iPad Air", "Tablette légère et puissante avec écran Liquid Retina", 699.99, 20, "Tablettes"));
        products.add(new Product(5, "Apple Watch Series 9", "Montre connectée avec capteurs santé avancés", 449.99, 30, "Montres"));
        products.add(new Product(6, "Magic Keyboard", "Clavier sans fil rechargeable", 119.99, 40, "Accessoires"));

        users.add(new User(1, "Jean Dupont", "jean@example.com", UserRole.CLIENT));
        users.add(new User(2, "Admin", "admin@chrionline.com", UserRole.ADMIN));
        users.add(new User(3, "Marie Martin", "marie@example.com", UserRole.CLIENT));

        Order o1 = new Order("CMD-1", "Jean Dupont", "jean@example.com", LocalDateTime.of(2026, 3, 5, 10, 30), 1479.98, OrderStatus.LIVREE);
        o1.addItem(new OrderItem("iPhone 15 Pro", 1, 1199.99));
        o1.addItem(new OrderItem("AirPods Pro", 1, 279.99));
        orders.add(o1);

        Order o2 = new Order("CMD-2", "Jean Dupont", "jean@example.com", LocalDateTime.of(2026, 3, 7, 14, 15), 569.98, OrderStatus.EN_ATTENTE);
        o2.addItem(new OrderItem("Magic Keyboard", 2, 119.99));
        o2.addItem(new OrderItem("iPad Air", 1, 699.99));
        orders.add(o2);

        Order o3 = new Order("CMD-3", "Jean Dupont", "jean@example.com", LocalDateTime.of(2026, 3, 8, 14, 28), 7499.97, OrderStatus.EN_ATTENTE);
        o3.addItem(new OrderItem("MacBook Pro 14\"", 3, 2499.99));
        orders.add(o3);
    }

    /** Connexion : email admin@chrionline.com = Admin, sinon Client. Mot de passe ignoré en mock. */
    public User login(String email, String password) {
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                currentUser = u;
                return u;
            }
        }
        User newUser = new User(users.size() + 1, email.split("@")[0], email, UserRole.CLIENT);
        users.add(newUser);
        currentUser = newUser;
        return currentUser;
    }

    public void logout() { currentUser = null; }
    public User getCurrentUser() { return currentUser; }

    public List<Product> getProducts() { return new ArrayList<>(products); }
    public Product getProduct(int id) {
        return products.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    public List<CartItem> getCart() {
        if (currentUser == null) return new ArrayList<>();
        return new ArrayList<>(cart);
    }

    public void addToCart(int productId, int quantity) {
        Product p = getProduct(productId);
        if (p == null) return;
        for (CartItem ci : cart) {
            if (ci.getProductId() == productId) {
                cart.remove(ci);
                cart.add(new CartItem(productId, p.getName(), ci.getQuantity() + quantity, p.getPrice()));
                return;
            }
        }
        cart.add(new CartItem(productId, p.getName(), quantity, p.getPrice()));
    }

    public void removeFromCart(int productId) {
        cart.removeIf(ci -> ci.getProductId() == productId);
    }

    public double getCartTotal() {
        return cart.stream().mapToDouble(CartItem::getTotal).sum();
    }

    public String validateOrder(String cardNumber, String expiry, String cvv) {
        if (cart.isEmpty()) return null;
        double total = getCartTotal();
        String orderId = "CMD-" + (orders.size() + 1);
        Order order = new Order(orderId, currentUser.getName(), currentUser.getEmail(), LocalDateTime.now(), total, OrderStatus.EN_ATTENTE);
        for (CartItem ci : cart) {
            order.addItem(new OrderItem(ci.getProductName(), ci.getQuantity(), ci.getUnitPrice()));
        }
        orders.add(order);
        cart.clear();
        return orderId;
    }

    public List<Order> getOrdersForCurrentUser() {
        if (currentUser == null) return new ArrayList<>();
        List<Order> list = new ArrayList<>();
        for (Order o : orders) {
            if (o.getClientEmail().equalsIgnoreCase(currentUser.getEmail()))
                list.add(o);
        }
        list.sort((a, b) -> b.getDate().compareTo(a.getDate()));
        return list;
    }

    public List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>(orders);
        list.sort((a, b) -> b.getDate().compareTo(a.getDate()));
        return list;
    }

    public List<User> getAllUsers() { return new ArrayList<>(users); }

    public void updateOrderStatus(String orderId, OrderStatus status) {
        for (Order o : orders) {
            if (o.getId().equals(orderId)) {
                o.setStatus(status);
                return;
            }
        }
    }

    public void addProduct(Product p) {
        int maxId = products.stream().mapToInt(Product::getId).max().orElse(0);
        products.add(new Product(maxId + 1, p.getName(), p.getDescription(), p.getPrice(), p.getStock(), p.getCategory()));
    }

    public void updateProduct(Product p) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == p.getId()) {
                products.set(i, p);
                return;
            }
        }
    }

    public void deleteProduct(int productId) {
        products.removeIf(p -> p.getId() == productId);
    }

    public int getOrdersCountToday() {
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        return (int) orders.stream().filter(o -> !o.getDate().isBefore(today)).count();
    }

    public double getTotalRevenue() {
        return orders.stream().mapToDouble(Order::getTotal).sum();
    }
}
