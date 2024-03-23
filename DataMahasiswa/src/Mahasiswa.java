public class Mahasiswa{
    private String nim;
    private String nama;
    private String jenisKelamin;
    private String nilai;
    private String angkatan;

    public Mahasiswa(String nim, String nama, String jenisKelamin, String nilai, String angkatan) {
        this.nim = nim;
        this.nama = nama;
        this.jenisKelamin = jenisKelamin;
        this.nilai = nilai;
        this.angkatan = angkatan;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setJenisKelamin(String jenisKelamin) {
        this.jenisKelamin = jenisKelamin;
    }

    public void setNilai(String nilai){ this.nilai =  nilai;}

    public void setAngkatan(String angkatan){ this.angkatan = angkatan;}

    public String getNim() {
        return this.nim;
    }

    public String getNama() {
        return this.nama;
    }

    public String getJenisKelamin() {
        return this.jenisKelamin;
    }

    public String getNilai(){ return this.nilai;}

    public String getAngkatan(){return this.angkatan;}
}
