package com.saurav.lld.vendingmachine;

import java.util.HashMap;

/**
 * Vending Machine: Requirements: - System should show all products - System
 * should handle payment options - System should create multiple denominations
 * (10, 20, 50, 100) - System should  *
 *
 *
 * Entry point for vending-machine. Add domain types in this package (or
 * subpackages), not in the default package.
 */
class 



class Product {

    String id;
    String name;

    public Product() {

    }
}

class ProductRepository {

}

class VendingMachine {

    HashMap<Product, Integer> productRecords = new HashMap<>();

    public void dispenseProduct(Cash ) {
        // first check cash amount, 
        // checks total value
        // checks if product and quantity available
        // 
    }

    public void addProduct(Product product, Integer quantity) {
        this.productRecords.put(product, quantity);
    }
}

public class Main {

    public static void main(String[] args) {
        System.out.println("vending-machine ready.");
    }
}
