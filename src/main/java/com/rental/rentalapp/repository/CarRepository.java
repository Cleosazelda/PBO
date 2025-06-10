package com.rental.rentalapp.repository;

import com.rental.rentalapp.model.Car;
import com.rental.rentalapp.model.enums.CarStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    
    // Method untuk mencari berdasarkan status
    List<Car> findByStatusKetersediaan(CarStatus status);
    
    // Method untuk count berdasarkan status
    long countByStatusKetersediaan(CarStatus status);
    
    // Method untuk filter berdasarkan status dan merek
    @Query("SELECT c FROM Car c WHERE " +
           "(:status IS NULL OR c.statusKetersediaan = :status) AND " +
           "(:merek IS NULL OR LOWER(c.merek) LIKE LOWER(CONCAT('%', :merek, '%')))")
    List<Car> findByFilter(@Param("status") CarStatus status, @Param("merek") String merek);
    
    // Method untuk mencari berdasarkan merek
    List<Car> findByMerekContainingIgnoreCase(String merek);
    
    // Method untuk mencari berdasarkan kode referensi
    Car findByKodeReferensi(String kodeReferensi);
    
    // Method untuk mencari berdasarkan plat nomor
    Car findByPlatNomor(String platNomor);
}