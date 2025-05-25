package org.store.data;

public class Kasa {
    private Cashier cashier;
    private Store store;

    public Kasa(Cashier cashier, Store store) {
        this.cashier = cashier;
        this.store = store;
    }

    public Cashier getCashier() {
        return cashier;
    }

    public void setCashier(Cashier cashier) {
        this.cashier = cashier;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }
}
