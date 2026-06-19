package com.codecli.monolith.dto;

public record SaveInvoice(String userId, Long[] productIds, boolean is_paid) {
}
