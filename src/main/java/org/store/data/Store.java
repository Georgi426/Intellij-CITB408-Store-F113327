package org.store.data;

import org.store.enums.StokaCategory;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Store {
    private HashSet<Cashier> cashiers;
    private Map<Stoka, Double> deliveredStoka;
    private Map<Stoka, Double> inventory;
    private Map<Stoka, Double> soldStoka;
    private HashSet<Receipt> issuedReceipts;
    private int countDaysForExpiryDateDiscount;
    private double expiryDateDiscount;
    private EnumMap<StokaCategory, Double> marginPercentByCategory;

    public Store(int countDaysForExpiryDateDiscount, double expiryDateDiscount, EnumMap<StokaCategory, Double> marginPercentByCategory) {
        this.cashiers = new HashSet<>();
        this.deliveredStoka = new HashMap<>();
        this.inventory = new HashMap<>();
        this.soldStoka = new HashMap<>();
        this.issuedReceipts = new HashSet<>();
        this.countDaysForExpiryDateDiscount = countDaysForExpiryDateDiscount;
        this.expiryDateDiscount = expiryDateDiscount;
        this.marginPercentByCategory = marginPercentByCategory;
    }

    public Map<Stoka, Double> getDeliveredStoka() {
        return deliveredStoka;
    }

    public HashSet<Cashier> getCashiers() {
        return cashiers;
    }

    public Map<Stoka, Double> getSoldStoka() {
        return soldStoka;
    }

    public Map<Stoka, Double> getInventory() {
        return inventory;
    }

    public int getCountDaysForExpiryDateDiscount() {
        return countDaysForExpiryDateDiscount;
    }

    public double getExpiryDateDiscount() {
        return expiryDateDiscount;
    }

    public EnumMap<StokaCategory, Double> getMarginPercentByCategory() {
        return marginPercentByCategory;
    }

    public void setSoldStoka(Map<Stoka, Double> soldStoka) {
        this.soldStoka = soldStoka;
    }

    public void setCashiers(HashSet<Cashier> cashiers) {
        this.cashiers = cashiers;
    }

    public void setInventory(Map<Stoka, Double> inventory) {
        this.inventory = inventory;
    }

    public void setDeliveredStoka(Map<Stoka, Double> deliveredStoka) {
        this.deliveredStoka = deliveredStoka;
    }

    public void addToInventory(Stoka stoka) {
        // Check if we already have this item in inventory
        if (this.inventory.containsKey(stoka)) {
            // Get current quantity
            double oldQuantity = this.inventory.get(stoka);
            // Add 1 to it
            double newQuantity = oldQuantity + 1;
            // Update inventory
            this.inventory.put(stoka, newQuantity);
        } else {
            // This is a new item, add it with quantity 1
            this.inventory.put(stoka, 1.0);
        }
    }
    
    public void addToDeliveredStoka(Stoka stoka) {
        // Check if we already have this item in delivered stoka
        if (this.deliveredStoka.containsKey(stoka)) {
            // Get current quantity
            double oldQuantity = this.deliveredStoka.get(stoka);
            // Add 1 to it
            double newQuantity = oldQuantity + 1;
            // Update delivered stoka
            this.deliveredStoka.put(stoka, newQuantity);
        } else {
            // This is a new item, add it with quantity 1
            this.deliveredStoka.put(stoka, 1.0);
        }
    }

    @Override
    public String toString() {
        return "Store{" +
                "cashiers=" + cashiers +
                ", deliveredStoka=" + deliveredStoka +
                ", inventory=" + inventory +
                ", soldStoka=" + soldStoka +
                ", issuedReceipts=" + issuedReceipts +
                ", countDaysForExpiryDateDiscount=" + countDaysForExpiryDateDiscount +
                ", expiryDateDiscount=" + expiryDateDiscount +
                ", marginPercentByCategory=" + marginPercentByCategory +
                '}';
    }

    public void addCashier(Cashier cashier) {

    }
}