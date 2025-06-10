package com.rental.rentalapp.model;
import com.rental.rentalapp.model.enums.*;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "rentals")
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal biayaSewa;

    @Enumerated(EnumType.STRING)
    private PaymentMethod metodePembayaran;

    @Enumerated(EnumType.STRING)
    private RentalStatus statusPenyewaan;

    private LocalDate tanggalMulai;
    private LocalDate tanggalSelesai;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    @ManyToOne
    @JoinColumn(name = "client_nik")
    private Client client;

    // Getter dan Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BigDecimal getBiayaSewa() { return biayaSewa; }
    public void setBiayaSewa(BigDecimal biayaSewa) { this.biayaSewa = biayaSewa; }
    public PaymentMethod getMetodePembayaran() { return metodePembayaran; }
    public void setMetodePembayaran(PaymentMethod metodePembayaran) { this.metodePembayaran = metodePembayaran; }
    public RentalStatus getStatusPenyewaan() { return statusPenyewaan; }
    public void setStatusPenyewaan(RentalStatus statusPenyewaan) { this.statusPenyewaan = statusPenyewaan; }
    public LocalDate getTanggalMulai() { return tanggalMulai; }
    public void setTanggalMulai(LocalDate tanggalMulai) { this.tanggalMulai = tanggalMulai; }
    public LocalDate getTanggalSelesai() { return tanggalSelesai; }
    public void setTanggalSelesai(LocalDate tanggalSelesai) { this.tanggalSelesai = tanggalSelesai; }
    public Car getCar() { return car; }
    public void setCar(Car car) { this.car = car; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
}
