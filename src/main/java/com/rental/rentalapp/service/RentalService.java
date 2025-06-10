package com.rental.rentalapp.service;

import com.rental.rentalapp.model.Rental;
import com.rental.rentalapp.model.Client;
import com.rental.rentalapp.model.Car;
import com.rental.rentalapp.repository.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Collections;

@Service
@Transactional
public class RentalService {
    private final RentalRepository rentalRepository;

    public RentalService(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    public List<Rental> getAllRentals() {
        try {
            return rentalRepository.findAll();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<Rental> findByClient(Client client) {
        try {
            return rentalRepository.findByClient(client);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public Optional<Rental> findById(Long id) {
        try {
            return rentalRepository.findById(id);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Rental saveRental(Rental rental) {
        // Validasi tanggal
        validateRentalDates(rental);
        
        // Validasi biaya sewa
        if (rental.getBiayaSewa() == null || rental.getBiayaSewa().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Biaya sewa harus lebih dari 0");
        }
        
        // Set default status jika belum ada
        if (rental.getStatusPenyewaan() == null) {
            rental.setStatusPenyewaan(com.rental.rentalapp.model.enums.RentalStatus.ACTIVE);
        }
        
        // Set default metode pembayaran jika belum ada
        if (rental.getMetodePembayaran() == null) {
            rental.setMetodePembayaran(com.rental.rentalapp.model.enums.PaymentMethod.QRIS);
        }
        
        return rentalRepository.save(rental);
    }

    public void deleteRental(Long id) {
        if (!rentalRepository.existsById(id)) {
            throw new IllegalArgumentException("Rental dengan ID " + id + " tidak ditemukan");
        }
        rentalRepository.deleteById(id);
    }
    
    public long countAllRentals() {
        try {
            return rentalRepository.count();
        } catch (Exception e) {
            return 0L;
        }
    }
    
    public boolean isCarAvailable(Car car, LocalDate startDate, LocalDate endDate, Long excludeRentalId) {
        // Method untuk cek ketersediaan mobil pada periode tertentu
        // Implementasi tergantung pada requirement bisnis
        return true; // Placeholder - implementasi sesuai kebutuhan
    }
    
    private void validateRentalDates(Rental rental) {
        if (rental.getTanggalMulai() == null) {
            throw new IllegalArgumentException("Tanggal mulai rental tidak boleh kosong");
        }
        
        if (rental.getTanggalSelesai() == null) {
            throw new IllegalArgumentException("Tanggal selesai rental tidak boleh kosong");
        }
        
        if (rental.getTanggalSelesai().isBefore(rental.getTanggalMulai())) {
            throw new IllegalArgumentException("Tanggal selesai tidak boleh lebih awal dari tanggal mulai");
        }
        
        if (rental.getTanggalMulai().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Tanggal mulai rental tidak boleh di masa lalu");
        }
    }
}