/*
Proiect la disciplina: Baza de Date
Titlu: Sistem Informatizat de Gestiune a unui Lanț de Policlinici
Realizat de: Staver Maxim, grupa 30223
*/
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FereastraSuperAdministrator extends JFrame {

    private JTextField tfCNP, tfLoginID, tfParola, tfNume, tfPrenume, tfAdresa, tfNrTel, tfEmail, tfIBAN, tfFunctie, tfNrContact;
    private JButton btnAdauga, btnModifica, btnSterge, btnIncarca;
    private JTable tabelUtilizatori;
    private DefaultTableModel model;

    public FereastraSuperAdministrator() {       // Poate modifica și utilizatori de tip Administrators
        setTitle("Fereastra Super Administrator");
        setLayout(new BorderLayout());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        // Panou pentru input cu GridBagLayout
        JPanel panouInput = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Margini între componente
        gbc.anchor = GridBagConstraints.WEST;

        // Adaugă etichetele și câmpurile de text
        gbc.gridx = 0; gbc.gridy = 0;
        panouInput.add(new JLabel("CNP:"), gbc);
        tfCNP = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 0;
        panouInput.add(tfCNP, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panouInput.add(new JLabel("Login ID:"), gbc);
        tfLoginID = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 1;
        panouInput.add(tfLoginID, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panouInput.add(new JLabel("Parola:"), gbc);
        tfParola = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 2;
        panouInput.add(tfParola, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panouInput.add(new JLabel("Nume:"), gbc);
        tfNume = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 3;
        panouInput.add(tfNume, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panouInput.add(new JLabel("Prenume:"), gbc);
        tfPrenume = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 4;
        panouInput.add(tfPrenume, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panouInput.add(new JLabel("Adresa:"), gbc);
        tfAdresa = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 5;
        panouInput.add(tfAdresa, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        panouInput.add(new JLabel("Nr. Tel:"), gbc);
        tfNrTel = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 6;
        panouInput.add(tfNrTel, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        panouInput.add(new JLabel("Email:"), gbc);
        tfEmail = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 7;
        panouInput.add(tfEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 8;
        panouInput.add(new JLabel("IBAN:"), gbc);
        tfIBAN = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 8;
        panouInput.add(tfIBAN, gbc);

        gbc.gridx = 0; gbc.gridy = 9;
        panouInput.add(new JLabel("Functie:"), gbc);
        tfFunctie = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 9;
        panouInput.add(tfFunctie, gbc);

        gbc.gridx = 0; gbc.gridy = 10;
        panouInput.add(new JLabel("Nr. Contact:"), gbc);
        tfNrContact = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 10;
        panouInput.add(tfNrContact, gbc);

        add(panouInput, BorderLayout.NORTH);

        // Tabel pentru utilizatori
        model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("CNP");
        model.addColumn("Login ID");
        model.addColumn("Nume");
        model.addColumn("Prenume");
        model.addColumn("Functie");
        model.addColumn("Data Angajarii");  // Coloana adăugată
        tabelUtilizatori = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(tabelUtilizatori);
        add(scrollPane, BorderLayout.CENTER);

        // Panou butoane
        JPanel panouButon = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnAdauga = new JButton("Adauga Utilizator");
        btnModifica = new JButton("Modifica Utilizator");
        btnSterge = new JButton("Sterge Utilizator");
        btnIncarca = new JButton("Incarca Utilizatori");

        // Personalizează butoanele
        btnAdauga.setBackground(Color.GREEN);
        btnModifica.setBackground(Color.ORANGE);
        btnSterge.setBackground(Color.RED);
        btnIncarca.setBackground(Color.CYAN);

        btnAdauga.setFocusPainted(false);
        btnModifica.setFocusPainted(false);
        btnSterge.setFocusPainted(false);
        btnIncarca.setFocusPainted(false);

        panouButon.add(btnAdauga);
        panouButon.add(btnModifica);
        panouButon.add(btnSterge);
        panouButon.add(btnIncarca);

        add(panouButon, BorderLayout.SOUTH);

        // Action Listeners pentru butoane
        btnAdauga.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adaugaUtilizator();
            }
        });

        btnModifica.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modificaUtilizator();
            }
        });

        btnSterge.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stergeUtilizator();
            }
        });

        btnIncarca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                incarcaUtilizatori();
            }
        });
    }

    // Metoda pentru adăugarea unui utilizator (Administrator sau Angajat)
    private void adaugaUtilizator() {
        String CNP = tfCNP.getText();
        String loginID = tfLoginID.getText();
        String parola = tfParola.getText();
        String nume = tfNume.getText();
        String prenume = tfPrenume.getText();
        String adresa = tfAdresa.getText();
        String nrTel = tfNrTel.getText();
        String email = tfEmail.getText();
        String IBAN = tfIBAN.getText();
        String functie = tfFunctie.getText();
        String nrContact = tfNrContact.getText();

        // Funcție pentru a adăuga și utilizatori Administrator
        if (UtilizatorDAO.adaugaUtilizator(CNP, loginID, parola, nume, prenume, adresa, nrTel, email, IBAN, functie, nrContact)) {
            JOptionPane.showMessageDialog(this, "Utilizator adăugat cu succes!");
            incarcaUtilizatori();  // Reîncarcă utilizatorii
        } else {
            JOptionPane.showMessageDialog(this, "Eroare la adăugarea utilizatorului.");
        }
    }

    // Alte metode (modificare, ștergere, încărcare utilizatori) sunt aceleași ca în clasa `FereastraAdministrator`
    private void modificaUtilizator() {
        int selectedRow = tabelUtilizatori.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) model.getValueAt(selectedRow, 0);

            // Extrage datele din câmpurile de text
            String CNP = tfCNP.getText();
            String loginID = tfLoginID.getText();
            String parola = tfParola.getText();
            String nume = tfNume.getText();
            String prenume = tfPrenume.getText();
            String adresa = tfAdresa.getText();
            String nrTel = tfNrTel.getText();
            String email = tfEmail.getText();
            String IBAN = tfIBAN.getText();
            String functie = tfFunctie.getText();
            String nrContact = tfNrContact.getText();

            // Permite modificarea rolului utilizatorului (admin sau angajat)
            String rolUtilizator = JOptionPane.showInputDialog(this, "Modifica rolul utilizatorului (admin/angajat):");

            // Validăm rolul introdus
            if (rolUtilizator == null || rolUtilizator.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Rolul utilizatorului nu poate fi gol!");
                return;
            }

            if (!rolUtilizator.equals("admin") && !rolUtilizator.equals("angajat")) {
                JOptionPane.showMessageDialog(this, "Rol invalid. Alegeți 'admin' sau 'angajat'.");
                return;
            }

            // Dacă rolul este valid, actualizăm utilizatorul
            if (UtilizatorDAO.actualizeazaUtilizator(id, CNP, loginID, parola, nume, prenume, adresa, nrTel, email, IBAN, functie, nrContact, rolUtilizator)) {
                JOptionPane.showMessageDialog(this, "Utilizator modificat cu succes!");
                incarcaUtilizatori();  // Reîncarcă utilizatorii
            } else {
                JOptionPane.showMessageDialog(this, "Eroare la modificarea utilizatorului.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selectează un utilizator din tabel pentru a-l modifica.");
        }
    }

    private void stergeUtilizator() {
        int selectedRow = tabelUtilizatori.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) model.getValueAt(selectedRow, 0);
            if (UtilizatorDAO.stergeUtilizator(id)) {
                JOptionPane.showMessageDialog(this, "Utilizator șters cu succes!");
                incarcaUtilizatori();  // Reîncarcă utilizatorii
            } else {
                JOptionPane.showMessageDialog(this, "Eroare la ștergerea utilizatorului.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selectează un utilizator din tabel pentru a-l șterge.");
        }
    }

    private void incarcaUtilizatori() {
        // Obține ResultSet-ul din DAO
        ResultSet rs = UtilizatorDAO.incarcaUtilizatori();
        try {
            model.setRowCount(0); // Curăță tabelul înainte de a adăuga noi date

            // Verifică dacă ResultSet-ul este valid înainte de a-l parcurge
            if (rs != null) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String CNP = rs.getString("CNP");
                    String loginID = rs.getString("loginID");
                    String nume = rs.getString("nume");
                    String prenume = rs.getString("prenume");
                    String functie = rs.getString("functie");
                    Date dataAngajarii = rs.getDate("data_angajarii"); // Adaugă data angajării

                    // Adaugă rândul în tabel
                    model.addRow(new Object[]{id, CNP, loginID, nume, prenume, functie, dataAngajarii});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                // Închide ResultSet-ul dacă nu este închis deja
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

