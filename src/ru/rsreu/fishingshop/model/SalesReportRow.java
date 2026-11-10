package ru.rsreu.fishingshop.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SalesReportRow(LocalDate orderDate, Long orderCount, BigDecimal totalSales, BigDecimal avgCheck) {
}