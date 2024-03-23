import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Menu extends JFrame{
    public static void main(String[] args) {
        // buat object window
        Menu window = new Menu();

        // atur ukuran window
        window.setSize(480, 560);

        // letakkan window di tengah layar
        window.setLocationRelativeTo(null);

        // isi window
        window.setContentPane(window.mainPanel);

        // ubah warna background
        window.getContentPane().setBackground(Color.white);

        // tampilkan window
        window.setVisible(true);

        // agar program ikut berhenti saat window diclose
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // index baris yang diklik
    private int selectedIndex = -1;
    // list untuk menampung semua mahasiswa
    private ArrayList<Mahasiswa> listMahasiswa;
    private Database database;

    private JPanel mainPanel;
    private JTextField nimField;
    private JTextField namaField;
    private JTable mahasiswaTable;
    private JButton addUpdateButton;
    private JButton cancelButton;
    private JComboBox jenisKelaminComboBox;
    private JButton deleteButton;
    private JLabel titleLabel;
    private JLabel nimLabel;
    private JLabel namaLabel;
    private JLabel jenisKelaminLabel;
    private JRadioButton Ang2023;
    private JRadioButton Ang2022;
    private JRadioButton Ang2020;
    private JRadioButton Ang2021;
    private JLabel nilaiLabel;
    private JTextField nilaifield;
    private JLabel angkatanLabel;
    private JComboBox angkatanComboBox;
    private JLabel newNimfield;
    private JTextField newnimfield;

    // constructor
    public Menu() {
        // inisialisasi listMahasiswa
        listMahasiswa = new ArrayList<>();

        // isi listMahasiswa
        database = new Database();

        // isi tabel mahasiswa
        mahasiswaTable.setModel(setTable());

        // ubah styling title
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));

        // atur isi combo box
        String[] jenisKelaminData = {"", "Laki-laki", "Perempuan"};
        jenisKelaminComboBox.setModel(new DefaultComboBoxModel(jenisKelaminData));

        String[] Angkatan = {"", "2020", "2021", "2022", "2023", "Alumni"};
        angkatanComboBox.setModel(new DefaultComboBoxModel(Angkatan));

        // sembunyikan button delete
        deleteButton.setVisible(false);

        newNimfield.setVisible(false);
        newnimfield.setVisible(false);

        // saat tombol add/update ditekan
        addUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedIndex == -1){
                    insertData();
                } else {
                    updateData();
                }
            }
        });

        // saat tombol delete ditekan
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedIndex >= 0){
                    deleteData();
                }
            }
        });

        // saat tombol cancel ditekan
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });

        // saat salah satu baris tabel ditekan
        mahasiswaTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // ubah selectedIndex menjadi baris tabel yang diklik
                selectedIndex = mahasiswaTable.getSelectedRow();

                if (selectedIndex >= 0) {
                    newNimfield.setVisible(true);
                    newnimfield.setVisible(true);
                    // simpan value textfield dan combo box
                    String selectedNIM = mahasiswaTable.getModel().getValueAt(selectedIndex, 1).toString();
                    nimField.setText(selectedNIM);
                    namaField.setText(mahasiswaTable.getModel().getValueAt(selectedIndex, 2).toString());
                    jenisKelaminComboBox.setSelectedItem(mahasiswaTable.getModel().getValueAt(selectedIndex, 3).toString());
                    nilaifield.setText(mahasiswaTable.getModel().getValueAt(selectedIndex, 4).toString());
                    angkatanComboBox.setSelectedItem(mahasiswaTable.getModel().getValueAt(selectedIndex, 5).toString());
                    newNimfield.setText("New Nim");
                    newnimfield.setText("");
                } else {
                    newNimfield.setVisible(false);
                    newnimfield.setVisible(false);
                    clearForm();
                }

                // ubah button "Add" menjadi "Update"
                addUpdateButton.setText("Update");
                // tampilkan button delete
                deleteButton.setVisible(true);
            }
        });
    }

    // SetTable
    public final DefaultTableModel setTable() {
        // tentukan kolom tabel
        Object[] column = {"No", "NIM", "Nama", "Jenis Kelamin", "Nilai", "Angkatan"};

        // buat objek tabel dengan kolom yang sudah dibuat
        DefaultTableModel temp = new DefaultTableModel(null, column);

        try {
            ResultSet resultSet = database.selectQuery("SELECT * FROM `mahasiswa`");

            int i = 0;
            while (resultSet.next()){
                Object[] row = new Object[6];

                row[0] = i + 1;
                row[1] = resultSet.getString("nim");
                row[2] = resultSet.getString("nama");
                row[3] = resultSet.getString("jenis_kelamin");
                row[4] = resultSet.getString("nilai");
                row[5] = resultSet.getString("angkatan");

                temp.addRow(row);

                i++;

            }
        } catch (SQLException e){
            throw  new RuntimeException(e);
        }

        return temp; // return juga harus diganti
    }

    public void insertData() {
        // ambil value dari textfield dan combobox
        String nim = nimField.getText();
        String nama = namaField.getText();
        String jenisKelamin = jenisKelaminComboBox.getSelectedItem().toString();
        String nilai = nilaifield.getText();
        String angkatan = angkatanComboBox.getSelectedItem().toString();

        // Validasi input kosong
        if (nim.isEmpty() || nama.isEmpty() || jenisKelamin.isEmpty() || nilai.isEmpty() || angkatan.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Semua kolom harus diisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Cek apakah NIM sudah ada dalam database
        try {
            ResultSet resultSet = database.selectQuery("SELECT COUNT(*) AS total FROM mahasiswa WHERE nim = '" + nim + "'");
            resultSet.next();
            int total = resultSet.getInt("total");
            if (total > 0) {
                JOptionPane.showMessageDialog(null, "NIM sudah ada dalam database.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat memeriksa NIM.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        // tambahkan data ke dalam database
        String sql = "INSERT INTO mahasiswa (nim, nama, jenis_kelamin, nilai, angkatan) VALUES ('" + nim + "', '" + nama + "', '" + jenisKelamin + "', '" + nilai + "', '" + angkatan + "')";
        int rowsAffected = database.insertUpdateDeleteQuery(sql);

        // Periksa apakah penyisipan berhasil
        if (rowsAffected > 0) {
            // update tabel
            mahasiswaTable.setModel(setTable());

            // bersihkan form
            clearForm();

            // feedback
            System.out.println("Insert Berhasil!");
            JOptionPane.showMessageDialog(null, "Data Berhasil Ditambahkan");
        } else {
            JOptionPane.showMessageDialog(null, "Gagal menambahkan data", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public void updateData() {
        // ambil data dari form
        String nimLama = nimField.getText();
        String nama = namaField.getText();
        String jenisKelamin = jenisKelaminComboBox.getSelectedItem().toString();
        String nilai = nilaifield.getText().toString();
        String angkatan = angkatanComboBox.getSelectedItem().toString();
        String nimBaru = newnimfield.getText().trim(); // tambahkan trim untuk menghapus spasi di awal dan akhir

        // Perbarui data mahasiswa di database
        String sql;
        if (!nimBaru.isEmpty()) { // periksa apakah nimBaru tidak kosong
            sql = "UPDATE mahasiswa SET nim = '" + nimBaru + "', nama = '" + nama + "', jenis_kelamin = '" + jenisKelamin + "', nilai = '" + nilai + "', angkatan = '" + angkatan + "' WHERE nim = '" + nimLama + "'";
        } else {
            sql = "UPDATE mahasiswa SET nama = '" + nama + "', jenis_kelamin = '" + jenisKelamin + "', nilai = '" + nilai + "', angkatan = '" + angkatan + "' WHERE nim = '" + nimLama + "'";
        }
        int rowsAffected = database.insertUpdateDeleteQuery(sql);

        // Periksa apakah pembaruan berhasil
        if (rowsAffected > 0) {
            // update tabel
            mahasiswaTable.setModel(setTable());

            // bersihkan form
            clearForm();

            // feedback
            JOptionPane.showMessageDialog(null, "Data Berhasil Diupdate");
        } else {
            JOptionPane.showMessageDialog(null, "Gagal melakukan pembaruan data", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public void deleteData() {
        if (selectedIndex >= 0) {
            // Ambil NIM dari data yang dipilih
            String nim = mahasiswaTable.getValueAt(selectedIndex, 1).toString();

            // Buat pernyataan SQL untuk menghapus data berdasarkan NIM
            String sql = "DELETE FROM mahasiswa WHERE nim = '" + nim + "'";

            // Jalankan pernyataan DELETE di database
            int rowsAffected = database.insertUpdateDeleteQuery(sql);

            // Periksa apakah penghapusan berhasil
            if (rowsAffected > 0) {
                // Update tabel
                mahasiswaTable.setModel(setTable());

                // Bersihkan form
                clearForm();

                // Beri umpan balik
                JOptionPane.showMessageDialog(null, "Data Berhasil Dihapus");
            } else {
                // Jika gagal, tampilkan pesan kesalahan
                JOptionPane.showMessageDialog(null, "Gagal menghapus data", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Tampilkan pesan jika tidak ada data yang dipilih untuk dihapus
            JOptionPane.showMessageDialog(null, "Tidak ada data yang dipilih untuk dihapus.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public void clearForm() {
        // kosongkan semua texfield dan combo box
        nimField.setText("");
        namaField.setText("");
        jenisKelaminComboBox.setSelectedItem("");
        nilaifield.setText("");
        angkatanComboBox.setSelectedItem("");

        // Jika kolom baru newnimfield dan newNimfield ada, kosongkan juga
        if (newnimfield.isVisible()) {
            newnimfield.setText("");
        }

        // ubah button "Update" menjadi "Add"
        addUpdateButton.setText("Add");

        // sembunyikan button delete
        deleteButton.setVisible(false);

        // ubah selectedIndex menjadi -1 (tidak ada baris yang dipilih)
        selectedIndex = -1;
    }

}


