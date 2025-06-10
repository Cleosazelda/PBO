package com.rental.rentalapp.service;

import com.rental.rentalapp.model.Client;
import com.rental.rentalapp.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Collections;

@Service
public class ClientService {
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> getAllClients() {
        try {
            return clientRepository.findAll();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public Optional<Client> findByNik(String nik) {
        try {
            return clientRepository.findById(nik);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Client> findByEmail(String email) {
        try {
            return Optional.ofNullable(clientRepository.findByEmail(email));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Client saveClient(Client client) {
        // Validasi data client
        validateClient(client);
        return clientRepository.save(client);
    }

    public void deleteClient(String nik) {
        if (!clientRepository.existsById(nik)) {
            throw new IllegalArgumentException("Client dengan NIK " + nik + " tidak ditemukan");
        }
        clientRepository.deleteById(nik);
    }

    public long countAllClients() {
        try {
            return clientRepository.count();
        } catch (Exception e) {
            return 0L;
        }
    }

    private void validateClient(Client client) {
        if (client.getNik() == null || client.getNik().trim().isEmpty()) {
            throw new IllegalArgumentException("NIK tidak boleh kosong");
        }
        
        if (client.getSim() == null || client.getSim().trim().isEmpty()) {
            throw new IllegalArgumentException("Nomor SIM tidak boleh kosong");
        }
        
        if (client.getNamaDepan() == null || client.getNamaDepan().trim().isEmpty()) {
            throw new IllegalArgumentException("Nama depan tidak boleh kosong");
        }
        
        if (client.getNamaBelakang() == null || client.getNamaBelakang().trim().isEmpty()) {
            throw new IllegalArgumentException("Nama belakang tidak boleh kosong");
        }
        
        if (client.getEmail() == null || client.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email tidak boleh kosong");
        }
        
        if (client.getTelepon() == null || client.getTelepon().trim().isEmpty()) {
            throw new IllegalArgumentException("Nomor telepon tidak boleh kosong");
        }
        
        if (client.getAlamat() == null || client.getAlamat().trim().isEmpty()) {
            throw new IllegalArgumentException("Alamat tidak boleh kosong");
        }
        
        // Validasi format email sederhana
        if (!client.getEmail().contains("@") || !client.getEmail().contains(".")) {
            throw new IllegalArgumentException("Format email tidak valid");
        }
        
        // Set tanggal pendaftaran jika belum ada
        if (client.getTanggalPendaftaran() == null) {
            client.setTanggalPendaftaran(java.time.LocalDate.now());
        }
    }
}