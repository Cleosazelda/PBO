package com.rental.rentalapp.service;

import com.rental.rentalapp.model.Car;
import com.rental.rentalapp.model.enums.CarStatus;
import com.rental.rentalapp.repository.CarRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Collections;

@Service
public class CarService {
    private final CarRepository carRepository;

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public List<Car> getAllCars() {
        try {
            return carRepository.findAll();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<Car> getAvailableCars() {
        try {
            return carRepository.findByStatusKetersediaan(CarStatus.TERSEDIA);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<Car> getCarsByStatus(CarStatus status) {
        try {
            return carRepository.findByStatusKetersediaan(status);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<Car> filterCars(CarStatus status, String merek) {
        try {
            return carRepository.findByFilter(status, merek);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public Optional<Car> getCarById(Long id) {
        try {
            return carRepository.findById(id);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Car saveCar(Car car) {
        // Validasi data car
        validateCar(car);
        return carRepository.save(car);
    }

    public void deleteCar(Long id) {
        if (!carRepository.existsById(id)) {
            throw new IllegalArgumentException("Mobil dengan ID " + id + " tidak ditemukan");
        }
        carRepository.deleteById(id);
    }

    public long countAllCars() {
        try {
            return carRepository.count();
        } catch (Exception e) {
            return 0L;
        }
    }

    private void validateCar(Car car) {
        if (car.getKodeReferensi() == null || car.getKodeReferensi().trim().isEmpty()) {
            throw new IllegalArgumentException("Kode referensi tidak boleh kosong");
        }
        
        if (car.getPlatNomor() == null || car.getPlatNomor().trim().isEmpty()) {
            throw new IllegalArgumentException("Plat nomor tidak boleh kosong");
        }
        
        if (car.getMerek() == null || car.getMerek().trim().isEmpty()) {
            throw new IllegalArgumentException("Merek mobil tidak boleh kosong");
        }
        
        if (car.getModel() == null || car.getModel().trim().isEmpty()) {
            throw new IllegalArgumentException("Model mobil tidak boleh kosong");
        }
        
        if (car.getTanggalRilis() != null && car.getTanggalRilis().isAfter(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("Tanggal rilis tidak boleh di masa depan");
        }
        
        if (car.getHargaSewa() == null || car.getHargaSewa().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Harga sewa harus lebih dari 0");
        }
        
        if (car.getStatusKetersediaan() == null) {
            car.setStatusKetersediaan(CarStatus.TERSEDIA);
        }
        
        if (car.getJarakTempuh() != null && car.getJarakTempuh() < 0) {
            throw new IllegalArgumentException("Jarak tempuh tidak boleh negatif");
        }
        
        if (car.getJumlahKursi() != null && (car.getJumlahKursi() < 1 || car.getJumlahKursi() > 50)) {
            throw new IllegalArgumentException("Jumlah kursi harus antara 1-50");
        }
    }
}