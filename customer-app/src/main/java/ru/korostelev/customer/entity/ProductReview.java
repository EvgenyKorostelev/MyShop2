package ru.korostelev.customer.entity;

import java.util.UUID;

public record ProductReview(UUID uuid, int id, int rating, String review) {
}
