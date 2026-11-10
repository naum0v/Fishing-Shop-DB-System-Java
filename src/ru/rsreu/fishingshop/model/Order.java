package ru.rsreu.fishingshop.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Order(Long id, LocalDateTime orderDateTime, String status, BigDecimal totalAmount, Long customerId,
		Long sellerId) {
}