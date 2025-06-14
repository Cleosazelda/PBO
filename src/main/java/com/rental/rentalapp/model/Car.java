package com.rental.rentalapp.model;
import com.rental.rentalapp.model.enums.*;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column(unique = true, nullable = false)
    // private String kodeReferensi;

    @Column(unique = true, nullable = false)
    private String platNomor;

    private String merek;
    private String model;
    private LocalDate tanggalRilis;
    private BigDecimal hargaSewa;

    @Enumerated(EnumType.STRING)
    private CarStatus statusKetersediaan;

    @Enumerated(EnumType.STRING)
    private FuelType jenisBahanBakar;

    private Integer jarakTempuh;

    @Enumerated(EnumType.STRING)
    private TransmissionType jenisTransmisi;

    private String warna;
    private Integer jumlahKursi;
    private String fiturTambahan;

    private boolean TERSEDIA = true;


    private String gambar; // bisa simpan path/file name

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private Agent agent;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL)
    private List<Rental> penyewaanList;

    // Getter dan Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    // public String getKodeReferensi() { return kodeReferensi; }
    // public void setKodeReferensi(String kodeReferensi) { this.kodeReferensi = kodeReferensi; }
    public String getPlatNomor() { return platNomor; }
    public void setPlatNomor(String platNomor) { this.platNomor = platNomor; }
    public String getMerek() { return merek; }
    public void setMerek(String merek) { this.merek = merek; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public LocalDate getTanggalRilis() { return tanggalRilis; }
    public void setTanggalRilis(LocalDate tanggalRilis) { this.tanggalRilis = tanggalRilis; }
    public BigDecimal getHargaSewa() { return hargaSewa; }
    public void setHargaSewa(BigDecimal hargaSewa) { this.hargaSewa = hargaSewa; }
    public CarStatus getStatusKetersediaan() { return statusKetersediaan; }
    public void setStatusKetersediaan(CarStatus statusKetersediaan) { this.statusKetersediaan = statusKetersediaan; }
    public FuelType getJenisBahanBakar() { return jenisBahanBakar; }
    public void setJenisBahanBakar(FuelType jenisBahanBakar) { this.jenisBahanBakar = jenisBahanBakar; }
    public Integer getJarakTempuh() { return jarakTempuh; }
    public void setJarakTempuh(Integer jarakTempuh) { this.jarakTempuh = jarakTempuh; }
    public TransmissionType getJenisTransmisi() { return jenisTransmisi; }
    public void setJenisTransmisi(TransmissionType jenisTransmisi) { this.jenisTransmisi = jenisTransmisi; }
    public String getWarna() { return warna; }
    public void setWarna(String warna) { this.warna = warna; }
    public Integer getJumlahKursi() { return jumlahKursi; }
    public void setJumlahKursi(Integer jumlahKursi) { this.jumlahKursi = jumlahKursi; }
    public String getFiturTambahan() { return fiturTambahan; }
    public void setFiturTambahan(String fiturTambahan) { this.fiturTambahan = fiturTambahan; }
    public String getGambar() { return gambar; }
    public void setGambar(String gambar) { this.gambar = gambar; }
    public Agent getAgent() { return agent; }
    public void setAgent(Agent agent) { this.agent = agent; }
    public List<Rental> getPenyewaanList() { return penyewaanList; }
    public void setPenyewaanList(List<Rental> penyewaanList) { this.penyewaanList = penyewaanList; }

}

