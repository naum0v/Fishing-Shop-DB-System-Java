package ru.rsreu.fishingshop.model;

public record Customer(Long id, String fullName, String phone, String email, String address) {
}