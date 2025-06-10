package com.rental.rentalapp.specification;

import com.rental.rentalapp.model.Car;
import com.rental.rentalapp.model.enums.CarStatus;
import com.rental.rentalapp.model.enums.TransmissionType;
import org.springframework.data.jpa.domain.Specification;

public class CarSpecifications {

    public static Specification<Car> hasStatus(CarStatus status) {
        return (root, query, builder) -> status == null ? null : builder.equal(root.get("statusKetersediaan"), status);
    }

    public static Specification<Car> hasTransmisi(TransmissionType transmisi) {
        return (root, query, builder) -> transmisi == null ? null : builder.equal(root.get("jenisTransmisi"), transmisi);
    }

    public static Specification<Car> hasJumlahKursi(Integer jumlahKursi) {
        return (root, query, builder) -> jumlahKursi == null ? null : builder.equal(root.get("jumlahKursi"), jumlahKursi);
    }

    public static Specification<Car> hasWarna(String warna) {
        return (root, query, builder) -> warna == null ? null :
                builder.like(builder.lower(root.get("warna")), "%" + warna.toLowerCase() + "%");
    }
}
