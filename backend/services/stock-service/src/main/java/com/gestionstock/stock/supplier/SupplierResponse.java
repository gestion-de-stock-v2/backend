package com.gestionstock.stock.supplier;

public record SupplierResponse(
        Integer id, String name, String registrationNumber, String phone, String email
) {}
