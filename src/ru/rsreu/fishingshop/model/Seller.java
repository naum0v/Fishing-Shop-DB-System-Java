package ru.rsreu.fishingshop.model;

import java.time.LocalDate;

public record Seller(Long id, String fullName, String phone, LocalDate hireDate) {
}