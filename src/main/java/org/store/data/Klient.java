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

    public void subtractMoney(BigDecimal amount) {


        if (money.compareTo(amount) >= 0) {
            this.money = this.money.subtract(amount);
        } else {
            throw new RuntimeException("Недостатъчно средства за покупка.");
        }
    }

    public void addToCart(Stoka stoka, double quantity) {
        if (quantity <= 0) {
            System.out.println("Невалидно количество.");
            return;
        }

        cart.merge(stoka, quantity, Double::sum);
        System.out.println("Добавени в количката: " + quantity + " бр. от " + stoka.getName());
    }

    public void clearCart() {
        cart.clear();
    }

    @Override
    public String toString() {
        return "Klient{" +
                "cart=" + cart +
                ", money=" + money +
                '}';
    }
}
