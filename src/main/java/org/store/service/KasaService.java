package org.store.service;

import org.store.data.*;
import org.store.exceptions.NotEnoughMoneyException;
import org.store.exceptions.NotEnoughStokaAvailableException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
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

        validateStokaAvailability(klient.getCart());


        BigDecimal totalPrice = calculateTotalPrice(klient.getCart());


        if (klient.getMoney().compareTo(totalPrice) < 0) {
            throw new NotEnoughMoneyException("Недостатъчно средства. Необходими: " + totalPrice + " лв., Налични: " + klient.getMoney() + " лв.");
        }


        String serialNumber = generateReceiptNumber();
        Cashier cashier = this.kasa.getCashier();
        LocalDate issueDate = LocalDate.now();
        Receipt receipt = new Receipt(serialNumber, cashier, issueDate, new HashMap<>(klient.getCart()));


        this.storeService.removeFromInventory(klient.getCart());
        this.storeService.addSoldStoka(klient.getCart());


        klient.subtractMoney(totalPrice);


        klient.clearCart();

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
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public String getCashierName() {
        return this.kasa.getCashier().getName();
    }
}
