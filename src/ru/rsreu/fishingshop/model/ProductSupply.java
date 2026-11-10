package ru.rsreu.fishingshop.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductSupply(Long id, Long supplierId, Long productId, LocalDate lastSupplyDate,
		BigDecimal purchasePrice, Boolean isPrimarySupplier) {
}