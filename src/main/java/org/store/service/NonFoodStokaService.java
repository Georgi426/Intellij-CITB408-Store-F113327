package org.store.service;

import org.store.data.NonFoodStoka;
import org.store.data.Stoka;
import org.store.enums.StokaCategory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NonFoodStokaService {
    
    /**
     * Създава нова нехранителна стока
     * @param id идентификатор
     * @param name име на стоката
     * @param deliveryPrice цена на доставка
     * @param price продажна цена
     * @return новата нехранителна стока
     */
    public NonFoodStoka createNonFoodStoka(String id, String name, BigDecimal deliveryPrice, BigDecimal price) {
        return new NonFoodStoka(id, name, deliveryPrice, price);
    }
    
    /**
     * Изчислява марж на печалба за нехранителна стока
     * @param nonFoodStoka нехранителна стока
     * @return процент на марж
     */
    public double calculateProfitMargin(NonFoodStoka nonFoodStoka) {
        BigDecimal deliveryPrice = nonFoodStoka.getDeliveryPrice();
        BigDecimal sellingPrice = nonFoodStoka.getPrice();
        
        if (deliveryPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Цената на доставка трябва да е положителна");
        }
        
        BigDecimal profit = sellingPrice.subtract(deliveryPrice);
        
        // (profit / deliveryPrice) * 100
        return profit.divide(deliveryPrice, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
    
    /**
     * Филтрира нехранителни стоки от списък със стоки
     * @param stokaList списък със стоки
     * @return списък само с нехранителни стоки
     */
    public List<NonFoodStoka> filterNonFoodItems(List<Stoka> stokaList) {
        return stokaList.stream()
                .filter(stoka -> stoka.getStokaCategory() == StokaCategory.NONFOOD)
                .filter(stoka -> stoka instanceof NonFoodStoka)
                .map(stoka -> (NonFoodStoka) stoka)
                .collect(Collectors.toList());
    }
    
    /**
     * Прилага отстъпка върху нехранителна стока
     * @param nonFoodStoka нехранителна стока
     * @param discountPercent процент отстъпка
     * @return стоката с обновена цена
     */
    public NonFoodStoka applyDiscount(NonFoodStoka nonFoodStoka, double discountPercent) {
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("Процентът на отстъпка трябва да е между 0 и 100");
        }
        
        BigDecimal currentPrice = nonFoodStoka.getPrice();
        BigDecimal discountAmount = currentPrice.multiply(BigDecimal.valueOf(discountPercent / 100));
        BigDecimal newPrice = currentPrice.subtract(discountAmount);
        
        nonFoodStoka.setPrice(newPrice);
        return nonFoodStoka;
    }
    
    /**
     * Изчислява общата стойност на нехранителни стоки
     * @param nonFoodStokaMap карта с нехранителни стоки и техните количества
     * @return общата стойност
     */
    public BigDecimal calculateTotalValueOfNonFoodItems(Map<NonFoodStoka, Double> nonFoodStokaMap) {
        BigDecimal total = BigDecimal.ZERO;
        
        for (Map.Entry<NonFoodStoka, Double> entry : nonFoodStokaMap.entrySet()) {
            NonFoodStoka nonFoodStoka = entry.getKey();
            Double quantity = entry.getValue();
            
            BigDecimal itemPrice = nonFoodStoka.getPrice().multiply(BigDecimal.valueOf(quantity));
            total = total.add(itemPrice);
        }
        
        return total;
    }
    
    /**
     * Сортира нехранителни стоки по марж на печалба
     * @param nonFoodStokaList списък с нехранителни стоки
     * @return сортиран списък
     */
    public List<NonFoodStoka> sortByProfitMargin(List<NonFoodStoka> nonFoodStokaList) {
        List<NonFoodStoka> sortedList = new ArrayList<>(nonFoodStokaList);
        
        sortedList.sort((s1, s2) -> {
            double margin1 = calculateProfitMargin(s1);
            double margin2 = calculateProfitMargin(s2);
            return Double.compare(margin2, margin1); // Descending order
        });
        
        return sortedList;
    }
    
    /**
     * Обновява цена на нехранителна стока, базирана на процент на печалба
     * @param nonFoodStoka нехранителна стока
     * @param targetProfitMargin целеви процент на печалба
     * @return стоката с обновена цена
     */
    public NonFoodStoka updatePriceBasedOnProfitMargin(NonFoodStoka nonFoodStoka, double targetProfitMargin) {
        if (targetProfitMargin < 0) {
            throw new IllegalArgumentException("Процентът на печалба трябва да е положителен");
        }
        
        BigDecimal deliveryPrice = nonFoodStoka.getDeliveryPrice();
        
        // newPrice = deliveryPrice * (1 + targetProfitMargin/100)
        BigDecimal marginMultiplier = BigDecimal.ONE.add(BigDecimal.valueOf(targetProfitMargin / 100));
        BigDecimal newPrice = deliveryPrice.multiply(marginMultiplier)
                .setScale(2, RoundingMode.HALF_UP);
        
        nonFoodStoka.setPrice(newPrice);
        return nonFoodStoka;
    }
    
    /**
     * Проверява дали нехранителната стока има печалба
     * @param nonFoodStoka нехранителна стока
     * @return true ако има печалба, false в противен случай
     */
    public boolean isProfitable(NonFoodStoka nonFoodStoka) {
        return nonFoodStoka.getPrice().compareTo(nonFoodStoka.getDeliveryPrice()) > 0;
    }
}
