package ru.rsreu.fishingshop.model;

import java.math.BigDecimal;

public record CustomerBonusRow(String customerName, String cardNumber, String levelName, BigDecimal accumulatedPoints,
		BigDecimal totalPurchases, Long totalOrders) {
}