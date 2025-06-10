package com.rental.rentalapp.model.enums;

public enum RentalStatus {
    ACTIVE("Aktif"),
    COMPLETED("Selesai"),
    CANCELLED("Dibatalkan"),
    PENDING("Menunggu"),
    OVERDUE("Terlambat");

    private final String displayName;

    RentalStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}