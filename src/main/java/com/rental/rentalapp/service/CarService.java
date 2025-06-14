package com.rental.rentalapp.service;

import com.rental.rentalapp.model.Agent;
import com.rental.rentalapp.model.Car;
import com.rental.rentalapp.model.enums.CarStatus;
import com.rental.rentalapp.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CarService {

    private final CarRepository carRepository;

    @Autowired
    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public List<Car> findCarsByAgent(Agent agent) {
        return carRepository.findByAgent(agent);
    }
    
    // Method baru untuk filter mobil berdasarkan agent, status, dan merek
    public List<Car> filterCarsByAgent(Agent agent, CarStatus status, String merek) {
        List<Car> agentCars = carRepository.findByAgent(agent);
        
        // Filter berdasarkan status jika ada
        if (status != null) {
            agentCars = agentCars.stream()
                    .filter(car -> car.getStatusKetersediaan() == status)
                    .collect(Collectors.toList());
        }
        
        // Filter berdasarkan merek jika ada
        if (StringUtils.hasText(merek)) {
            agentCars = agentCars.stream()
                    .filter(car -> car.getMerek() != null && 
                            car.getMerek().toLowerCase().contains(merek.toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        return agentCars;
    }
   
    public List<Car> getAvailableCars() {
        return carRepository.findByStatusKetersediaan(CarStatus.TERSEDIA);
    }

    public Optional<Car> getCarById(Long id) {
        return carRepository.findById(id);
    }

    public void saveCar(Car car) {
        validateCar(car);
        
        // Jika edit, pastikan kode referensi tidak berubah
        // if (car.getId() != null) {
        //     Optional<Car> existingCar = carRepository.findById(car.getId());
        //     if (existingCar.isPresent()) {
        //         car.setKodeReferensi(existingCar.get().getKodeReferensi());
        //     }
        // }
        
        carRepository.save(car);
    }

    public void deleteCar(Long id) {
        if (!carRepository.existsById(id)) {
            throw new RuntimeException("Mobil dengan ID " + id + " tidak ditemukan.");
        }
        carRepository.deleteById(id);
    }

    public long countAllCars() {
        return carRepository.count();
    }
   
    public List<Car> filterCars(CarStatus status, String merek) {
        if (status == null && !StringUtils.hasText(merek)) {
            return carRepository.findAll();
        }
        return carRepository.findByFilter(status, merek);
    }

    // public Car findByKodeReferensi(String kodeReferensi) {
    //     return carRepository.findByKodeReferensi(kodeReferensi);
    // }

    public Car findByPlatNomor(String platNomor) {
        return carRepository.findByPlatNomor(platNomor);
    }

    private void validateCar(Car car) {
    if (car.getMerek() == null || car.getMerek().trim().isEmpty()) {
        throw new IllegalArgumentException("Merek tidak boleh kosong");
    }
    if (car.getModel() == null || car.getModel().trim().isEmpty()) {
        throw new IllegalArgumentException("Model tidak boleh kosong");
    }
    if (car.getPlatNomor() == null || car.getPlatNomor().trim().isEmpty()) {
        throw new IllegalArgumentException("Plat nomor tidak boleh kosong");
    }
    if (car.getHargaSewa() == null || car.getHargaSewa().compareTo(java.math.BigDecimal.ZERO) <= 0) {
        throw new IllegalArgumentException("Harga sewa harus lebih dari 0");
    }
    if (car.getStatusKetersediaan() == null) {
        throw new IllegalArgumentException("Status ketersediaan tidak boleh kosong");
    }

    if (car.getId() != null) {
        Car existingByPlat = carRepository.findByPlatNomor(car.getPlatNomor());
        if (existingByPlat != null && !existingByPlat.getId().equals(car.getId())) {
            throw new IllegalArgumentException("Plat nomor sudah digunakan oleh mobil lain");
        }
    } else {
        Car existingByPlat = carRepository.findByPlatNomor(car.getPlatNomor());
        if (existingByPlat != null) {
            throw new IllegalArgumentException("Plat nomor sudah digunakan");
            }
        }
    }

    public Optional<Car> findById(Long id) {
    return getCarById(id);
}
}

