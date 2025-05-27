package org.store.data;

import org.store.enums.StokaCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Stoka {
    private UUID id;
    private String name;
    private BigDecimal deliveryPrice;
    private BigDecimal price;
    private LocalDate expirationDate;
    private StokaCategory stokaCategory;

    public Stoka(String id, String name, BigDecimal deliveryPrice, BigDecimal price,
                 LocalDate expirationDate, StokaCategory stokaCategory) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.deliveryPrice = deliveryPrice;
        this.price = price;
        this.expirationDate = expirationDate;
        this.stokaCategory = stokaCategory;
    }

    public BigDecimal getDeliveryPrice() {
        return deliveryPrice;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public StokaCategory getStokaCategory() {
        return stokaCategory;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Stoka{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", deliveryPrice=" + deliveryPrice +
                ", price=" + price +
                ", expirationDate=" + expirationDate +
                ", stokaCategory=" + stokaCategory +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stoka stoka = (Stoka) o;
        return Objects.equals(id, stoka.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public boolean isExpired() {
        LocalDate today = LocalDate.now();
        LocalDate expDate = this.expirationDate;
        boolean isBeforeToday = expDate.isBefore(today);

        if (isBeforeToday == true) {
            System.out.println("ВНИМАНИЕ: Продуктът " + this.name + " е с изтекъл срок на годност!");
            return true;
        }

        return false;
    }

    public boolean isValid() {
        return !this.isExpired();
    }

    public void setExpirationDate(LocalDate newExpirationDate) {
        this.expirationDate = newExpirationDate;
    }

    public void setDeliveryPrice(BigDecimal deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }
}

