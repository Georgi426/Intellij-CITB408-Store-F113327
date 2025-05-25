package org.store.data;

public class Cart {
    private Stoka stoka;
    private double quantity;

    @Override
    public String toString() {
        return "Cart{" +
                "stoka=" + stoka +
                ", quantity=" + quantity +
                '}';
    }
}