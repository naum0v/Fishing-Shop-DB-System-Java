package ru.rsreu.fishingshop.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoyaltyCard(Long id, String cardNumber, LocalDate registrationDate, BigDecimal accumulatedPoints,
		Long customerId, Long levelId) {
}