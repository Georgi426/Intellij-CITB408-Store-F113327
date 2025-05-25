package org.store.service;

import org.store.data.*;
import org.store.exceptions.NotEnoughMoneyException;
import org.store.exceptions.NotEnoughStokaAvailableException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public class KasaService {
    private Kasa kasa;
    private StoreService storeService;

    public KasaService(Kasa kasa, StoreService storeService) {
        this.kasa = kasa;
        this.storeService = storeService;
    }

    public Receipt checkout(Klient klient) {
        // Check if there's enough stock in inventory
        validateStokaAvailability(klient.getCart());

        // Calculate total price
        BigDecimal totalPrice = calculateTotalPrice(klient.getCart());

        // Check if client has enough money
        if (klient.getMoney().compareTo(totalPrice) < 0) {
            throw new NotEnoughMoneyException("Client doesn't have enough money. Required: " + totalPrice + ", Available: " + klient.getMoney());
        }

        // Generate receipt
        String serialNumber = generateReceiptNumber();
        Cashier cashier = this.kasa.getCashier();
        LocalDate issueDate = LocalDate.now();
        
        Receipt receipt = new Receipt(serialNumber, cashier, issueDate, klient.getCart());

        // Update store inventory
        this.storeService.removeFromInventory(klient.getCart());
        
        // Add sold items to store's records
        this.storeService.addSoldStoka(klient.getCart());

        // Return receipt
        return receipt;
    }

    private void validateStokaAvailability(Map<Stoka, Double> cart) {
        Map<Stoka, Double> inventory = this.kasa.getStore().getInventory();

        for (Map.Entry<Stoka, Double> entry : cart.entrySet()) {
            Stoka stoka = entry.getKey();
            Double requestedQuantity = entry.getValue();

            if (!inventory.containsKey(stoka)) {
                throw new NotEnoughStokaAvailableException(stoka.getName(), requestedQuantity);
            }
            
            Double availableQuantity = inventory.get(stoka);
            if (availableQuantity < requestedQuantity) {
                throw new NotEnoughStokaAvailableException(stoka.getName(), requestedQuantity - availableQuantity);
            }
        }
    }

    private BigDecimal calculateTotalPrice(Map<Stoka, Double> cart) {
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Stoka, Double> entry : cart.entrySet()) {
            Stoka stoka = entry.getKey();
            Double quantity = entry.getValue();

            BigDecimal price = stoka.getPrice();
            BigDecimal quantityAsBigDecimal = new BigDecimal(quantity.toString());
            BigDecimal itemTotal = price.multiply(quantityAsBigDecimal);

            total = total.add(itemTotal);
        }

        return total;
    }

    private String generateReceiptNumber() {
        // Generate a unique receipt number using UUID
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public String getCashierName() {
        return this.kasa.getCashier().getName();
    }
}


