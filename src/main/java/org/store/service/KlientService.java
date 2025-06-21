package org.store.service;

import org.store.data.Klient;
import org.store.data.Stoka;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KlientService {
    



    public BigDecimal addMoney(Klient klient, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумата трябва да е положителна");
        }
        
        BigDecimal currentMoney = klient.getMoney();
        BigDecimal newBalance = currentMoney.add(amount);
        

        return newBalance;
    }
    

    public BigDecimal calculateCartTotal(Klient klient) {
        Map<Stoka, Double> cart = klient.getCart();
        BigDecimal total = BigDecimal.ZERO;
        
        for (Map.Entry<Stoka, Double> entry : cart.entrySet()) {
            Stoka stoka = entry.getKey();
            Double quantity = entry.getValue();
            
            BigDecimal price = stoka.getPrice();
            BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(quantity));
            
            total = total.add(itemTotal);
        }
        
        return total;
    }
    



    public boolean hasEnoughFunds(Klient klient) {
        BigDecimal cartTotal = calculateCartTotal(klient);
        return klient.getMoney().compareTo(cartTotal) >= 0;
    }
    


    public boolean removeFromCart(Klient klient, Stoka stoka) {
        Map<Stoka, Double> cart = klient.getCart();
        
        if (cart.containsKey(stoka)) {
            cart.remove(stoka);
            return true;
        }
        
        return false;
    }


    public boolean updateCartItemQuantity(Klient klient, Stoka stoka, double newQuantity) {
        if (newQuantity <= 0) {
            return removeFromCart(klient, stoka);
        }
        
        Map<Stoka, Double> cart = klient.getCart();
        
        if (cart.containsKey(stoka)) {
            cart.put(stoka, newQuantity);
            return true;
        }
        
        return false;
    }
    


    public List<Stoka> getCartItems(Klient klient) {
        return new ArrayList<>(klient.getCart().keySet());
    }
    


    public boolean isCartEmpty(Klient klient) {
        return klient.getCart().isEmpty();
    }

    public double getTotalItemCount(Klient klient) {
        double total = 0;
        
        for (Double quantity : klient.getCart().values()) {
            total += quantity;
        }
        
        return total;
    }


    public void copyCart(Klient source, Klient target) {
        Map<Stoka, Double> sourceCart = source.getCart();
        
        for (Map.Entry<Stoka, Double> entry : sourceCart.entrySet()) {
            target.addToCart(entry.getKey(), entry.getValue());
        }
    }
    


    public Stoka getMostExpensiveItem(Klient klient) {
        Map<Stoka, Double> cart = klient.getCart();
        
        if (cart.isEmpty()) {
            return null;
        }
        
        Stoka mostExpensive = null;
        BigDecimal highestPrice = BigDecimal.ZERO;
        
        for (Stoka stoka : cart.keySet()) {
            if (mostExpensive == null || stoka.getPrice().compareTo(highestPrice) > 0) {
                mostExpensive = stoka;
                highestPrice = stoka.getPrice();
            }
        }
        
        return mostExpensive;
    }
}
