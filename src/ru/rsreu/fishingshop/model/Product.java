package ru.rsreu.fishingshop.model;

import java.math.BigDecimal;

public record Product(Long id, String name, String description, BigDecimal price, Integer stockQuantity,
		Long categoryId) {
}