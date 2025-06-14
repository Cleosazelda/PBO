package com.rental.rentalapp.service;

import com.rental.rentalapp.model.enums.CarStatus;
import com.rental.rentalapp.model.enums.PaymentMethod;
import com.rental.rentalapp.model.enums.RentalStatus;
import com.rental.rentalapp.model.Rental;
import com.rental.rentalapp.model.Client;
import com.rental.rentalapp.model.Car;
import com.rental.rentalapp.repository.RentalRepository;
import com.rental.rentalapp.repository.CarRepository; // Tambahkan import ini jika CarService tidak memiliki saveCar()

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled; // Tambahkan import ini

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RentalService {

    private final RentalRepository rentalRepository;
    private final CarService carService; // Gunakan CarService untuk manipulasi Car

    // Asumsi CarService sudah memiliki method saveCar
    // Jika tidak, Anda mungkin perlu menginjeksikan CarRepository juga
    // private final CarRepository carRepository; 

    public RentalService(RentalRepository rentalRepository, CarService carService) {
        this.rentalRepository = rentalRepository;
        this.carService = carService;
    }

    public List<Rental> getAllRentals() {
        try {
            return rentalRepository.findAll();
        } catch (Exception e) {
            // Log the exception, don't just return empty list silently in a real app
            System.err.println("Error fetching all rentals: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Rental> findByClient(Client client) {
        try {
            return rentalRepository.findByClient(client);
        } catch (Exception e) {
            System.err.println("Error fetching rentals by client: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public Optional<Rental> findById(Long id) {
        try {
            return rentalRepository.findById(id);
        } catch (Exception e) {
            System.err.println("Error finding rental by ID: " + e.getMessage());
            return Optional.empty();
        }
    }

    public Rental saveRental(Rental rental) {
        // Validasi tanggal
        validateRentalDates(rental);

        // Validasi biaya sewa
        if (rental.getBiayaSewa() == null || rental.getBiayaSewa().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Biaya sewa harus lebih dari 0");
        }

        // Ambil objek Car dari rental. Penting untuk memastikan Car tidak null.
        Car car = rental.getCar();
        if (car == null || car.getId() == null) {
            throw new IllegalArgumentException("Mobil tidak boleh kosong atau tidak valid.");
        }
        
        // Dapatkan mobil dari database untuk memastikan kita bekerja dengan entitas terkelola
        // Ini penting jika objek 'car' yang datang dari form belum 'managed'
        Optional<Car> existingCarOpt = carService.findById(car.getId());
        if (existingCarOpt.isEmpty()) {
            throw new IllegalArgumentException("Mobil dengan ID " + car.getId() + " tidak ditemukan.");
        }
        Car managedCar = existingCarOpt.get();


        // Set status default penyewaan jika baru
        if (rental.getId() == null && rental.getStatusPenyewaan() == null) {
             // Secara default, jika rental baru, set ke PENDING atau ACTIVE sesuai kebijakan
            rental.setStatusPenyewaan(RentalStatus.PENDING); // Lebih aman dimulai dari PENDING
        } else if (rental.getStatusPenyewaan() == null) {
             // Jika ada update tapi statusnya null, pertahankan status sebelumnya atau set default
            rental.setStatusPenyewaan(RentalStatus.ACTIVE); // Misalnya default ke ACTIVE
        }

        // Validasi ketersediaan mobil hanya jika rental berstatus ACTIVE atau PENDING
        if (rental.getStatusPenyewaan() == RentalStatus.ACTIVE || rental.getStatusPenyewaan() == RentalStatus.PENDING) {
            if (!isCarAvailable(managedCar, rental.getTanggalMulai(), rental.getTanggalSelesai(), rental.getId())) {
                throw new IllegalArgumentException("Mobil " + managedCar.getMerek() + " " + managedCar.getModel() + " tidak tersedia pada periode tanggal tersebut.");
            }
        }

        // Set status mobil menjadi sedang disewakan jika rental AKTIF
        if (rental.getStatusPenyewaan() == RentalStatus.ACTIVE) {
            managedCar.setStatusKetersediaan(CarStatus.SEDANG_DISEWAKAN);
            carService.saveCar(managedCar); // Simpan perubahan status mobil
        } else if (rental.getStatusPenyewaan() == RentalStatus.COMPLETED || rental.getStatusPenyewaan() == RentalStatus.CANCELLED) {
            // Jika rental selesai atau dibatalkan, set status mobil menjadi TERSEDIA
            managedCar.setStatusKetersediaan(CarStatus.TERSEDIA);
            carService.saveCar(managedCar);
        }
        
        // Set metode pembayaran default (jika belum ada)
        if (rental.getMetodePembayaran() == null) {
            rental.setMetodePembayaran(PaymentMethod.QRIS);
        }

        return rentalRepository.save(rental);
    }

    public void deleteRental(Long id) {
        Optional<Rental> rentalOpt = rentalRepository.findById(id);
        if (rentalOpt.isPresent()) {
            Rental rental = rentalOpt.get();
            Car car = rental.getCar();
            
            // Sebelum menghapus rental, kembalikan status mobil menjadi TERSEDIA
            if (car != null && car.getStatusKetersediaan() == CarStatus.SEDANG_DISEWAKAN) {
                car.setStatusKetersediaan(CarStatus.TERSEDIA);
                carService.saveCar(car);
            }
            rentalRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Rental dengan ID " + id + " tidak ditemukan");
        }
    }

    public long countAllRentals() {
        try {
            return rentalRepository.count();
        } catch (Exception e) {
            System.err.println("Error counting all rentals: " + e.getMessage());
            return 0L;
        }
    }

    // Perbaikan implementasi isCarAvailable
    public boolean isCarAvailable(Car car, LocalDate startDate, LocalDate endDate, Long excludeRentalId) {
        // Cek apakah ada rental lain yang konflik dalam periode tanggal tersebut
        long conflictingRentalsCount = rentalRepository.countConflictingRentals(car, startDate, endDate, excludeRentalId);
        return conflictingRentalsCount == 0;
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
        
        // Memungkinkan tanggal mulai di hari ini atau di masa depan
        if (rental.getTanggalMulai().isBefore(LocalDate.now())) {
            // Jika ini rental baru, jangan izinkan di masa lalu
            if (rental.getId() == null) { 
                throw new IllegalArgumentException("Tanggal mulai rental tidak boleh di masa lalu untuk rental baru");
            }
            // Untuk rental yang sudah ada, kita mungkin membiarkan tanggal mulai di masa lalu (misal: untuk melihat riwayat)
        }
    }

    /**
     * Method ini akan berjalan setiap hari pada pukul 00:00 (tengah malam) WIB
     * untuk mengecek rental yang sudah selesai dan mengupdate status mobil.
     * Menggunakan cron expression "0 0 0 * * ?" yang berarti pada detik 0, menit 0, jam 0, setiap hari, setiap bulan, setiap hari dalam seminggu.
     */
    @Scheduled(cron = "0 0 0 * * ?") 
    @Transactional // Penting untuk memastikan semua perubahan disimpan dalam satu transaksi
    public void updateCarStatusForCompletedRentals() {
        LocalDate today = LocalDate.now(); // Ambil tanggal hari ini
        System.out.println("Menjalankan tugas terjadwal untuk memperbarui status mobil pada " + today);

        // Cari semua rental yang AKTIF dan tanggal selesainya SUDAH LEWAT hari ini
        // Misalnya, jika tanggal selesai adalah kemarin, maka hari ini sudah dianggap selesai
        List<Rental> completedRentals = rentalRepository.findByStatusPenyewaanAndTanggalSelesaiBefore(RentalStatus.ACTIVE, today);

        if (completedRentals.isEmpty()) {
            System.out.println("Tidak ada rental yang selesai hari ini.");
            return;
        }

        for (Rental rental : completedRentals) {
            Car car = rental.getCar();
            if (car != null && car.getStatusKetersediaan() == CarStatus.SEDANG_DISEWAKAN) {
                // Periksa apakah mobil ini tidak memiliki rental aktif lain yang tumpang tindih
                // Ini penting agar mobil tidak tiba-tiba tersedia jika ada rental berkelanjutan
                long activeOverlappingRentals = rentalRepository.countConflictingRentals(car, LocalDate.now(), LocalDate.MAX, rental.getId());

                if (activeOverlappingRentals == 0) { // Jika tidak ada rental aktif lain untuk mobil ini
                    car.setStatusKetersediaan(CarStatus.TERSEDIA);
                    carService.saveCar(car); // Simpan perubahan status mobil
                    System.out.println("Mobil " + car.getMerek() + " " + car.getModel() + " (ID: " + car.getId() + ") statusnya menjadi TERSEDIA karena rental ID " + rental.getId() + " selesai.");
                } else {
                    System.out.println("Mobil " + car.getMerek() + " " + car.getModel() + " (ID: " + car.getId() + ") masih memiliki rental aktif lainnya. Status tidak diubah.");
                }
                
                // Opsional: Ubah juga status rental menjadi COMPLETED
                rental.setStatusPenyewaan(RentalStatus.COMPLETED);
                rentalRepository.save(rental); // Simpan perubahan status rental
            }
        }
        System.out.println("Tugas terjadwal selesai.");
    }
}