package com.rental.rentalapp.repository;

import com.rental.rentalapp.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
    
    // Method untuk mencari berdasarkan email
    Client findByEmail(String email);
    
    // Method untuk mencari berdasarkan nomor telepon
    Client findByTelepon(String telepon);
    
    // Method untuk mencari berdasarkan SIM
    Client findBySim(String sim);
    
    // Method untuk mencari berdasarkan nama depan
    List<Client> findByNamaDepanContainingIgnoreCase(String namaDepan);
    
    // Method untuk mencari berdasarkan nama belakang
    List<Client> findByNamaBelakangContainingIgnoreCase(String namaBelakang);
    
    // Method untuk mencari berdasarkan nama lengkap
    List<Client> findByNamaDepanContainingIgnoreCaseOrNamaBelakangContainingIgnoreCase(
            String namaDepan, String namaBelakang);
}