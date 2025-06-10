package com.rental.rentalapp.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @Column(length = 20)
    private String nik;

    @Column(length = 20, nullable = false)
    private String sim;

    private String namaDepan;
    private String namaBelakang;
    private String alamat;
    private String telepon;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private LocalDate tanggalLahir;
    private LocalDate tanggalPendaftaran;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Rental> daftarPenyewaan;

    // Getter dan Setter
    public String getNik() { return nik; }
    public void setNik(String nik) { this.nik = nik; }
    public String getSim() { return sim; }
    public void setSim(String sim) { this.sim = sim; }
    public String getNamaDepan() { return namaDepan; }
    public void setNamaDepan(String namaDepan) { this.namaDepan = namaDepan; }
    public String getNamaBelakang() { return namaBelakang; }
    public void setNamaBelakang(String namaBelakang) { this.namaBelakang = namaBelakang; }
    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }
    public String getTelepon() { return telepon; }
    public void setTelepon(String telepon) { this.telepon = telepon; }
    public LocalDate getTanggalLahir() { return tanggalLahir; }
    public void setTanggalLahir(LocalDate tanggalLahir) { this.tanggalLahir = tanggalLahir; }
    public LocalDate getTanggalPendaftaran() { return tanggalPendaftaran; }
    public void setTanggalPendaftaran(LocalDate tanggalPendaftaran) { this.tanggalPendaftaran = tanggalPendaftaran; }
    public List<Rental> getDaftarPenyewaan() { return daftarPenyewaan; }
    public void setDaftarPenyewaan(List<Rental> daftarPenyewaan) { this.daftarPenyewaan = daftarPenyewaan; }
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}
}   
