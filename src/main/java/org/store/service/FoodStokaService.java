package org.store.service;

import org.store.data.FoodStoka;
import org.store.data.Stoka;
import org.store.enums.StokaCategory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FoodStokaService {
    
    /**
     * Създава нова хранителна стока
     * @param id идентификатор
     * @param name име на стоката
     * @param deliveryPrice цена на доставка
     * @param price продажна цена
     * @param expirationDate срок на годност
     * @return новата хранителна стока
     */
    public FoodStoka createFoodStoka(String id, String name, BigDecimal deliveryPrice, 
                                      BigDecimal price, LocalDate expirationDate) {
        if (expirationDate == null || expirationDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Срокът на годност трябва да е валидна бъдеща дата");
        }
        
        return new FoodStoka(id, name, deliveryPrice, price, expirationDate);
    }
    
    /**
     * Проверява дали хранителната стока е близо до изтичане на срока на годност
     * @param foodStoka хранителна стока
     * @param daysThreshold праг в дни
     * @return true ако е близо до изтичане, false в противен случай
     */
    public boolean isNearExpiration(FoodStoka foodStoka, int daysThreshold) {
        LocalDate today = LocalDate.now();
        LocalDate expirationDate = foodStoka.getExpirationDate();
        
        if (expirationDate == null) {
            return false;
        }
        
        long daysUntilExpiration = ChronoUnit.DAYS.between(today, expirationDate);
        return daysUntilExpiration >= 0 && daysUntilExpiration <= daysThreshold;
    }
    
    /**
     * Прилага отстъпка за хранителни стоки, близки до изтичане
     * @param foodStoka хранителна стока
     * @param daysThreshold праг в дни
     * @param discountPercent процент отстъпка
     * @return стоката с обновена цена
     */
    public FoodStoka applyExpirationDiscount(FoodStoka foodStoka, int daysThreshold, double discountPercent) {
        if (isNearExpiration(foodStoka, daysThreshold)) {
            BigDecimal currentPrice = foodStoka.getPrice();
            BigDecimal discountAmount = currentPrice.multiply(BigDecimal.valueOf(discountPercent / 100));
            BigDecimal newPrice = currentPrice.subtract(discountAmount);
            
            foodStoka.setPrice(newPrice);
            return foodStoka;
        }
        
        return foodStoka;
    }
    
    /**
     * Филтрира хранителни стоки от списък със стоки
     * @param stokaList списък със стоки
     * @return списък само с хранителни стоки
     */
    public List<FoodStoka> filterFoodItems(List<Stoka> stokaList) {
        return stokaList.stream()
                .filter(stoka -> stoka.getStokaCategory() == StokaCategory.FOOD)
                .filter(stoka -> stoka instanceof FoodStoka)
                .map(stoka -> (FoodStoka) stoka)
                .collect(Collectors.toList());
    }
    
    /**
     * Филтрира изтекли хранителни стоки
     * @param foodStokaList списък с хранителни стоки
     * @return списък с изтекли хранителни стоки
     */
    public List<FoodStoka> filterExpiredFoodItems(List<FoodStoka> foodStokaList) {
        return foodStokaList.stream()
                .filter(FoodStoka::isExpired)
                .collect(Collectors.toList());
    }
    
    /**
     * Филтрира хранителни стоки, близки до изтичане
     * @param foodStokaList списък с хранителни стоки
     * @param daysThreshold праг в дни
     * @return списък с хранителни стоки, близки до изтичане
     */
    public List<FoodStoka> filterNearExpirationFoodItems(List<FoodStoka> foodStokaList, int daysThreshold) {
        return foodStokaList.stream()
                .filter(foodStoka -> isNearExpiration(foodStoka, daysThreshold))
                .collect(Collectors.toList());
    }
    
    /**
     * Сортира хранителни стоки по срок на годност (възходящо)
     * @param foodStokaList списък с хранителни стоки
     * @return сортиран списък
     */
    public List<FoodStoka> sortByExpirationDate(List<FoodStoka> foodStokaList) {
        List<FoodStoka> sortedList = new ArrayList<>(foodStokaList);
        sortedList.sort(Comparator.comparing(FoodStoka::getExpirationDate));
        return sortedList;
    }
    
    /**
     * Изчислява процент на прясност на хранителна стока
     * @param foodStoka хранителна стока
     * @return процент на прясност (0-100)
     */
    public double calculateFreshnessPercentage(FoodStoka foodStoka) {
        LocalDate today = LocalDate.now();
        LocalDate expirationDate = foodStoka.getExpirationDate();
        
        if (expirationDate == null || expirationDate.isBefore(today)) {
            return 0.0;
        }
        
        // Приемаме, че типичният срок на годност е 30 дни
        long daysUntilExpiration = ChronoUnit.DAYS.between(today, expirationDate);
        return Math.min(100.0, (daysUntilExpiration / 30.0) * 100.0);
    }
    
    /**
     * Удължава срока на годност на хранителна стока
     * @param foodStoka хранителна стока
     * @param days брой дни за удължаване
     * @return стоката с обновен срок на годност
     */
    public FoodStoka extendExpirationDate(FoodStoka foodStoka, int days) {
        if (days <= 0) {
            throw new IllegalArgumentException("Дните за удължаване трябва да са положително число");
        }
        
        LocalDate currentExpirationDate = foodStoka.getExpirationDate();
        LocalDate newExpirationDate = currentExpirationDate.plusDays(days);
        
        foodStoka.setExpirationDate(newExpirationDate);
        return foodStoka;
    }
    
    /**
     * Определя приоритет на продажба на хранителни стоки
     * @param foodStokaList списък с хранителни стоки
     * @return сортиран списък по приоритет на продажба (първо тези с близък срок на годност)
     */
    public List<FoodStoka> prioritizeForSelling(List<FoodStoka> foodStokaList) {
        List<FoodStoka> sortedList = new ArrayList<>(foodStokaList);
        sortedList.sort(Comparator.comparing(FoodStoka::getExpirationDate));
        return sortedList;
    }
    
    /**
     * Прилага сезонни отстъпки за хранителни стоки
     * @param foodStokaList списък с хранителни стоки
     * @param seasonalItems имена на сезонни стоки
     * @param discountPercent процент отстъпка
     * @return списък с обновени цени
     */
    public List<FoodStoka> applySeasonalDiscounts(List<FoodStoka> foodStokaList, 
                                                 List<String> seasonalItems, 
                                                 double discountPercent) {
        for (FoodStoka foodStoka : foodStokaList) {
            if (seasonalItems.contains(foodStoka.getName())) {
                BigDecimal currentPrice = foodStoka.getPrice();
                BigDecimal discountAmount = currentPrice.multiply(BigDecimal.valueOf(discountPercent / 100));
                BigDecimal newPrice = currentPrice.subtract(discountAmount);
                foodStoka.setPrice(newPrice);
            }
        }
        
        return foodStokaList;
    }
    
    /**
     * Изчислява средна трайност на хранителни стоки
     * @param foodStokaList списък с хранителни стоки
     * @return средна трайност в дни
     */
    public double calculateAverageShelfLife(List<FoodStoka> foodStokaList) {
        if (foodStokaList.isEmpty()) {
            return 0.0;
        }
        
        LocalDate today = LocalDate.now();
        double totalDays = 0.0;
        
        for (FoodStoka foodStoka : foodStokaList) {
            LocalDate expirationDate = foodStoka.getExpirationDate();
            if (expirationDate != null && !expirationDate.isBefore(today)) {
                long days = ChronoUnit.DAYS.between(today, expirationDate);
                totalDays += days;
            }
        }
        
        return totalDays / foodStokaList.size();
    }
}
