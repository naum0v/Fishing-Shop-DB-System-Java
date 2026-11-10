package ru.rsreu.fishingshop.model;

import java.math.BigDecimal;

public record OrderItem(Long id, Long orderId, Long productId, Integer quantity, BigDecimal unitPrice) {
}