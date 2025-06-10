package com.rental.rentalapp.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "agents")
public class Agent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String namaDepan;
    private String namaBelakang;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "agent", cascade = CascadeType.ALL)
    private List<Car> daftarMobil;

    // Getter dan Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNamaDepan() { return namaDepan; }
    public void setNamaDepan(String namaDepan) { this.namaDepan = namaDepan; }
    public String getNamaBelakang() { return namaBelakang; }
    public void setNamaBelakang(String namaBelakang) { this.namaBelakang = namaBelakang; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public List<Car> getDaftarMobil() { return daftarMobil; }
    public void setDaftarMobil(List<Car> daftarMobil) { this.daftarMobil = daftarMobil; }
}

