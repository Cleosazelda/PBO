package com.rental.rentalapp.model.enums;

public enum CarStatus {
    TERSEDIA("Tersedia"),
    SEDANG_DISEWAKAN("Sedang Disewakan"),
    SEDANG_PERAWATAN("Sedang Perawatan");

    private final String displayName;

    CarStatus(String displayName) {
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