package ru.korostelev.customer.entity;

import lombok.Data;


public record Product(int id, String title, String description) {
}
