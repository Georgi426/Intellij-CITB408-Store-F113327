package org.store.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Klient {
    private Map<Stoka, Double> cart;
    private BigDecimal money;

    public Klient(BigDecimal money) {
        this.cart = new HashMap<>();
        this.money = money;
    }

    public Map<Stoka, Double> getCart() {
        return cart;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void addToCart(Stoka stoka, double quantity) {
        // First check if the item is not expired
        LocalDate todayDate = LocalDate.now();
        LocalDate expirationDate = stoka.getExpirationDate();
        
        boolean isNotExpired = expirationDate.isAfter(todayDate);
        
        // We only add non-expired items
        if (isNotExpired == true) {
            // Check if we already have this item in the cart
            if (this.cart.containsKey(stoka)) {
                // If yes, get the current quantity
                double oldQuantity = this.cart.get(stoka);
                // Add the new quantity
                double newQuantity = oldQuantity + quantity;
                // Update the cart
                this.cart.put(stoka, newQuantity);
                System.out.println("Added " + quantity + " more of " + stoka.getName() + " to cart!");
            } else {
                // If not, just add it with the given quantity
                this.cart.put(stoka, quantity);
                System.out.println("Added " + stoka.getName() + " to cart for the first time!");
            }
        } else {
            // If expired, print a message
            System.out.println("Cannot add expired item to cart: " + stoka.getName());
        }
    }

    @Override
    public String toString() {
        return "Klient{" +
                "cart=" + cart +
                ", money=" + money +
                '}';
    }
}
