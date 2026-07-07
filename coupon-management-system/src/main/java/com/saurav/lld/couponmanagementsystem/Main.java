package com.saurav.lld.couponmanagementsystem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Entry point for coupon-management-system. Add domain types in this package
 * (or subpackages), not in the default package.
 *
 * Design and implement a Coupon Management System for Meesho's e-commerce
 * platform.
 *
 * The system should handle product purchases, coupon applications, and payment
 * processing.
 *
 * The goal is to create an efficient and user-friendly system that encourages
 * purchases through strategic discount offerings.
 *
 * Requirements
 *
 * Users can browse available products with their prices (product catalog) Users
 * can add multiple items to their cart (no limit on max cart value or number of
 * products) Users can view available coupons for their purchase Coupons should
 * have an upper limit if giving a percentage discount Users can apply a coupon
 * to their cart. Two kinds of coupons - Flat discount, Percentage based System
 * should validate if the coupon is: Not expired Applicable to the current
 * purchase amount Not already used by the user System should calculate final
 * payment amount after coupon application Users can complete the purchase with
 * the discounted amount (placeOrder) Users can view their purchase history with
 * applied coupons (order history) Users can remove items from cart System
 * should maintain coupon usage history (across all users) Design should be
 * extensible for future requirements, scalable, and thread safe for concurrent
 * access
 */
class User {

    String name;
    String email;
    Cart cart;

    User(String id, String name, String email) {
        this.name = name;
        this.email = email;
        this.cart = new Cart(id);
    }

}

class Product {

    String id;
    String name;
    Number price;

    Product(String id, String name, Double price) {
        this.id = id;
        this.price = price;
        this.name = name;
    }
}

class Coupon {

    enum Type {
        FLAT, PERCENTAGE
    }

    String code;
    String description;
    Type type;
    double value, maxDiscount, minCartValue;
    LocalDateTime expTime;

    public void Coupon(Type type, Double value,
            Double maxDiscount,
            Double minCartValue,
            String code,
            String descriptions,
            LocalDateTime expDateTime) {
        this.code = code;
        this.description = descriptions;
        this.maxDiscount = maxDiscount;
        this.value = value;
        this.minCartValue = minCartValue;
        this.type = type;
        this.expTime = expDateTime;
    }

    double calculateDiscount(double cartTotal) {
        if (type == Type.FLAT) {
            return Math.min(value, cartTotal);
        }

    }

    boolean isExpired() {
        return LocalDateTime.now().isAfter(expTime);
    }
}

class CouponService {

    Map<String, User> userRecords = new ConcurrentHashMap<>();

}

class ProductService {

    // list of product records
    Map<String, Product> productRecords = new ConcurrentHashMap<>();

    ProductService() {

    }

    void addProduct(Product product) {
        this.productRecords.put(product.id, product);
    }

    // return list of available products
    List<Product> showAllProducts() {
        List<Product> res = new ArrayList<>();
        for (Product product : productRecords.values()) {
            if (product.isAvailable()) {
                res.add(product);
            }
        }
        return res;
    }
}

class Cart {

    String userId;
    Map<Product, Integer> items = new HashMap<>();

    Cart(String id) {
        this.userId = id;
    }

    void addItem(Product prod, Integer quantity) {
        items.put(prod, quantity);
    }

    void removeItem(Product prod) {
        items.remove(prod);
    }

    void clearCart() {
        items.clear();
    }

    boolean isEmpty() {
        return items.isEmpty();
    }

    double totalValue() {
        double total = 0;
        for (Map.Entry<Product, Integer> entry : items.entrySet()) {
            total += entry.getValue() * entry.getKey().price.doubleValue();
        }
        return total;
    }

}

class CartManager {

    // list of carts for userId/accountId
    // userId - cartId
    Map<String, String> userCartRecord = new HashMap<>();

    // cartRecords
    Map<String, List<String>> cartRecords = new HashMap<>();

    void addItem(String cartId, String productId) {
        List<> cartRecords
        .put(cartId, productId);
    }

}

public class Main {

    public static void main(String[] args) {
        System.out.println("coupon-management-system ready.");

        Product p1 = new Product("prod-1", "t-shirt", 200.0);
        Product p2 = new Product("prod-2", "pant", 400.0);

        ProductService productSvc = new ProductService();
        productSvc.addProduct(p1);
        productSvc.addProduct(p2);

        System.out.println("Show All Available Products: ");
        List<Product> prods = productSvc.showAllProducts();
        for (Product p : prods) {
            System.out.println("product name: " + p.name + ", price: " + p.price);
        }

        CartManager cartManager = new CartManager();

        cartManager.addItem(cartId, productId);

    }
}
