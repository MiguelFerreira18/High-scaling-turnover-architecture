package com.codecli.monolith.dto;

public record SaveInvoice(String userId, long[] productIds, boolean is_paid) {
}
