package org.store.data;

import org.store.enums.StokaCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Stoka {
    private String id;
    private String name;
    private BigDecimal deliveryPrice;
    private BigDecimal price;
    private LocalDate expirationDate;
    private StokaCategory stokaCategory;

    public Stoka(String id, String name, BigDecimal deliveryPrice, BigDecimal price,
                 LocalDate expirationDate, StokaCategory stokaCategory) {
        this.id = id;
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
        return id.equals(stoka.id) && name.equals(stoka.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
    
    /**
     * Проверява дали продуктът е с изтекъл срок на годност
     * @return true ако продуктът е с изтекъл срок, false ако не е
     */
    public boolean isExpired() {
        // Вземаме днешната дата
        LocalDate today = LocalDate.now();
        
        // Вземаме срока на годност
        LocalDate expDate = this.expirationDate;
        
        // Проверяваме дали срокът на годност е преди днешната дата
        // Ако е преди днес, значи е изтекъл
        boolean isBeforeToday = expDate.isBefore(today);
        
        // Ако срокът на годност е преди днес, продуктът е изтекъл
        if (isBeforeToday == true) {
            System.out.println("ВНИМАНИЕ: Продуктът " + this.name + " е с изтекъл срок на годност!");
            return true;
        }
        
        // В противен случай, продуктът не е изтекъл
        return false;
    }
    
    /**
     * Проверява дали продуктът е валиден (с неизтекъл срок)
     * @return true ако продуктът е валиден, false ако не е
     */
    public boolean isValid() {
        // Просто връщаме обратното на isExpired
        return !this.isExpired();
    }
    
    /**
     * Задава нова дата на годност на продукта
     * @param newExpirationDate новата дата на годност
     */
    public void setExpirationDate(LocalDate newExpirationDate) {
        this.expirationDate = newExpirationDate;
    }

    public void setDeliveryPrice(BigDecimal deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }
}

