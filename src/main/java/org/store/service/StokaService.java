package org.store.service;

import org.store.data.Stoka;
import org.store.enums.StokaCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class StokaService {
    
    /**
     * Проверява дали стоката е близо до изтичане на срока на годност
     * @param stoka стоката за проверка
     * @param daysThreshold броят дни, под които се счита за близко изтичане
     * @return true ако стоката е близо до изтичане, false в противен случай
     */
    public boolean isNearExpiration(Stoka stoka, int daysThreshold) {
        if (stoka.getExpirationDate() == null) {
            return false; // Non-food items don't expire
        }
        
        LocalDate today = LocalDate.now();
        LocalDate expirationDate = stoka.getExpirationDate();
        
        long daysUntilExpiration = ChronoUnit.DAYS.between(today, expirationDate);
        return daysUntilExpiration >= 0 && daysUntilExpiration <= daysThreshold;
    }
    
    /**
     * Изчислява цена с отстъпка за стоки, близки до изтичане
     * @param stoka стоката за изчисление
     * @param daysThreshold броят дни, под които се прилага отстъпка
     * @param discountPercent процент отстъпка
     * @return цената след отстъпка или оригиналната цена
     */
    public BigDecimal calculatePriceWithDiscount(Stoka stoka, int daysThreshold, double discountPercent) {
        if (isNearExpiration(stoka, daysThreshold)) {
            BigDecimal discount = stoka.getPrice().multiply(BigDecimal.valueOf(discountPercent / 100));
            return stoka.getPrice().subtract(discount);
        }
        return stoka.getPrice();
    }
    
    /**
     * Филтрира списък от стоки по категория
     * @param stokaList списък от стоки
     * @param category категорията за филтриране
     * @return филтриран списък от стоки
     */
    public List<Stoka> filterByCategory(List<Stoka> stokaList, StokaCategory category) {
        return stokaList.stream()
                .filter(stoka -> stoka.getStokaCategory() == category)
                .collect(Collectors.toList());
    }
    
    /**
     * Филтрира списък от стоки, които са изтекли
     * @param stokaList списък от стоки
     * @return списък от изтекли стоки
     */
    public List<Stoka> filterExpiredItems(List<Stoka> stokaList) {
        return stokaList.stream()
                .filter(Stoka::isExpired)
                .collect(Collectors.toList());
    }
    
    /**
     * Сортира стоките по цена (възходящо)
     * @param stokaList списък от стоки
     * @return сортиран списък от стоки
     */
    public List<Stoka> sortByPrice(List<Stoka> stokaList) {
        List<Stoka> sortedList = new ArrayList<>(stokaList);
        sortedList.sort((s1, s2) -> s1.getPrice().compareTo(s2.getPrice()));
        return sortedList;
    }
    
    /**
     * Изчислява общата стойност на списък от стоки
     * @param stokaQuantityMap карта от стоки и техните количества
     * @return общата стойност на стоките
     */
    public BigDecimal calculateTotalValue(Map<Stoka, Double> stokaQuantityMap) {
        BigDecimal total = BigDecimal.ZERO;
        
        for (Map.Entry<Stoka, Double> entry : stokaQuantityMap.entrySet()) {
            Stoka stoka = entry.getKey();
            Double quantity = entry.getValue();
            
            BigDecimal itemPrice = stoka.getPrice().multiply(BigDecimal.valueOf(quantity));
            total = total.add(itemPrice);
        }
        
        return total;
    }
    
    /**
     * Обновява цена на стока с нова стойност
     * @param stoka стоката за обновяване
     * @param newPrice новата цена
     * @return обновената стока
     */
    public Stoka updatePrice(Stoka stoka, BigDecimal newPrice) {
        if (newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Цената трябва да е положително число");
        }
        stoka.setPrice(newPrice);
        return stoka;
    }
    
    /**
     * Обновява цена на доставка на стока
     * @param stoka стоката за обновяване
     * @param newDeliveryPrice новата цена на доставка
     * @return обновената стока
     */
    public Stoka updateDeliveryPrice(Stoka stoka, BigDecimal newDeliveryPrice) {
        if (newDeliveryPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Цената на доставка не може да бъде отрицателна");
        }
        stoka.setDeliveryPrice(newDeliveryPrice);
        return stoka;
    }
    
    /**
     * Удължава срока на годност на стоката с определен брой дни
     * @param stoka стоката за обновяване
     * @param days брой дни за удължаване
     * @return обновената стока
     */
    public Stoka extendExpirationDate(Stoka stoka, int days) {
        if (stoka.getExpirationDate() == null) {
            return stoka; // Non-food items don't have expiration dates
        }
        
        if (days <= 0) {
            throw new IllegalArgumentException("Дните за удължаване трябва да са положително число");
        }
        
        LocalDate newDate = stoka.getExpirationDate().plusDays(days);
        stoka.setExpirationDate(newDate);
        return stoka;
    }
}
