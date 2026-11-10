package ru.rsreu.fishingshop.model;

import java.math.BigDecimal;

public record SupplierEfficiencyRow(String supplierName, Long totalSupplies, BigDecimal totalPurchase,
		BigDecimal avgPrice) {
}