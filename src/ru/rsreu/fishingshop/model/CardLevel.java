package ru.rsreu.fishingshop.model;

import java.math.BigDecimal;

public record CardLevel(Long id, String name, BigDecimal discountPercent, BigDecimal bonusMultiplier) {
}