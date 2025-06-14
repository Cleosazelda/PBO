package com.rental.rentalapp.repository;

import com.rental.rentalapp.model.Rental;
import com.rental.rentalapp.model.Client;
import com.rental.rentalapp.model.Car;
import com.rental.rentalapp.model.enums.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    
    // Method untuk mencari berdasarkan client
    List<Rental> findByClient(Client client);
    
    // Method untuk mencari berdasarkan car
    List<Rental> findByCar(Car car);
    
    // Method untuk mencari berdasarkan status
    List<Rental> findByStatusPenyewaan(RentalStatus status);
    
    // Method untuk mencari 5 rental terbaru
    List<Rental> findTop5ByOrderByIdDesc();
    
    // Method untuk mencari rental berdasarkan tanggal
    List<Rental> findByTanggalMulaiBetween(LocalDate startDate, LocalDate endDate);
    
    // Method untuk mencari rental yang aktif
    List<Rental> findByStatusPenyewaanAndTanggalSelesaiAfter(RentalStatus status, LocalDate date);
    
    // Method untuk cek konflik rental (mobil sudah disewa pada periode tertentu)
    @Query("SELECT COUNT(r) FROM Rental r WHERE r.car = :car AND " +
            "r.statusPenyewaan IN ('ACTIVE', 'PENDING') AND " +
            "((r.tanggalMulai <= :endDate AND r.tanggalSelesai >= :startDate)) AND " +
            "(:excludeId IS NULL OR r.id != :excludeId)")
    long countConflictingRentals(@Param("car") Car car, 
                                 @Param("startDate") LocalDate startDate, 
                                 @Param("endDate") LocalDate endDate,
                                 @Param("excludeId") Long excludeId);
    
    // Method untuk mencari rental berdasarkan client NIK
    @Query("SELECT r FROM Rental r WHERE r.client.nik = :nik")
    List<Rental> findByClientNik(@Param("nik") String nik);
    
    // Method untuk mencari rental berdasarkan car ID
    @Query("SELECT r FROM Rental r WHERE r.car.id = :carId")
    List<Rental> findByCarId(@Param("carId") Long carId);

    // BARU: Method untuk mencari rental yang statusnya ACTIVE dan tanggal selesainya sudah sebelum tanggal tertentu
    List<Rental> findByStatusPenyewaanAndTanggalSelesaiBefore(RentalStatus status, LocalDate date);
}