package com.rental.rentalapp.service;

import com.rental.rentalapp.model.Client;
import com.rental.rentalapp.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ClientService(ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Optional<Client> findByNik(String nik) {
        return clientRepository.findById(nik);
    }

    public Optional<Client> findByEmail(String email) {
        return Optional.ofNullable(clientRepository.findByEmail(email));
    }
    
    // Method untuk registrasi client baru
    public void registerClient(Client client) {
        client.setPassword(passwordEncoder.encode(client.getPassword()));
        client.setTanggalPendaftaran(LocalDate.now());
        clientRepository.save(client);
    }

    // Method untuk menyimpan/update client dari sisi Admin
    public void saveClient(Client client) {
        // Jika password diisi (saat membuat baru atau reset), maka enkripsi
        // Jika password kosong (saat edit), jangan ubah password lama
        if (client.getPassword() != null && !client.getPassword().isEmpty()) {
            client.setPassword(passwordEncoder.encode(client.getPassword()));
        } else {
            // Ambil password lama dari database jika ada
            clientRepository.findById(client.getNik()).ifPresent(existingClient -> {
                client.setPassword(existingClient.getPassword());
            });
        }
        clientRepository.save(client);
    }
    
    public void deleteClient(String nik) {
        clientRepository.deleteById(nik);
    }

    public long countAllClients() {
        return clientRepository.count();
    }

    // Method untuk compatibility dengan Long ID (jika diperlukan)
public Optional<Client> findById(Long id) {
    // Karena Client menggunakan NIK (String), kita bisa convert jika diperlukan
    // Atau bisa return empty jika tidak applicable
    return Optional.empty();
}

// Method untuk compatibility dengan String ID
public Optional<Client> findById(String nik) {
    return clientRepository.findById(nik);
}
}