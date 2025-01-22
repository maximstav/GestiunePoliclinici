/*
Proiect la disciplina: Baza de Date
Titlu: Sistem Informatizat de Gestiune a unui Lanț de Policlinici
Realizat de: Staver Maxim, grupa 30223
*/
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import javax.swing.table.TableColumn;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;


public class FereastraAngajat extends JFrame {

    private String loginID;
    private String tipAngajat;

    public FereastraAngajat(String loginID) {
        this.loginID = loginID;

        // Obține tipul de angajat din baza de date
        this.tipAngajat = UtilizatorDAO.obtineTipAngajat(loginID);

        if (tipAngajat == null) {
            JOptionPane.showMessageDialog(this, "Eroare: Utilizatorul nu există!");
            return; // Ieși din constructor dacă nu s-a găsit utilizatorul
        }

        // În funcție de tipul de angajat, se vor apela funcții diferite
        switch (tipAngajat) {
            case "resurse-umane":
                fereastraResurseUmane();
                break;
            case "economic":
                fereastraEconomic();
                break;
            case "medical":
                fereastraMedical(loginID);
                break;
            default:
                JOptionPane.showMessageDialog(this, "Tip de angajat necunoscut!");
                break;
        }

        // Obține informațiile detaliate despre angajat
        Map<String, String> infoAngajat = UtilizatorDAO.getInfoAngajat(loginID);
        if (infoAngajat != null) {
            // Creează un JPanel pentru a arăta informațiile angajatului
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5); // Spațiu între elemente

            int row = 0;
            for (Map.Entry<String, String> entry : infoAngajat.entrySet()) {
                // Eticheta
                JLabel label = new JLabel(entry.getKey() + ":");
                label.setFont(new Font("Arial", Font.BOLD, 14));
                label.setForeground(new Color(0, 0, 0));
                gbc.gridx = 0;
                gbc.gridy = row;
                gbc.anchor = GridBagConstraints.WEST;
                infoPanel.add(label, gbc);

                // Valoarea
                JLabel value = new JLabel(entry.getValue());
                value.setFont(new Font("Arial", Font.PLAIN, 14));
                gbc.gridx = 1;
                infoPanel.add(value, gbc);
                row++;
            }

            // Adaugă un border pentru a face panelul mai vizibil
            infoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Informații Angajat", TitledBorder.LEFT, TitledBorder.TOP));

            // Adaugă panelul cu informațiile despre angajat în fereastră
            add(infoPanel, BorderLayout.CENTER);
        }

        // Inițializare fereastră
        setTitle("Fereastra Angajat");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrează fereastra pe ecran
        setResizable(false); // Fereastra nu poate fi redimensionată
        setVisible(true);
    }

    //============================================================================
    //===== Fereastra RESURSE UMANE
    //=============================================================================
    public static void fereastraResurseUmane() {
        JFrame frame = new JFrame("Gestionare Resurse Umane");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Panou de căutare
        JPanel panelSearch = new JPanel(new GridLayout(2, 4, 5, 5));

        JLabel lblNume = new JLabel("Nume:");
        JTextField txtNume = new JTextField();
        JLabel lblPrenume = new JLabel("Prenume:");
        JTextField txtPrenume = new JTextField();
        JLabel lblFunctie = new JLabel("Funcție:");
        JTextField txtFunctie = new JTextField();
        JButton btnCauta = new JButton("Caută");

        panelSearch.add(lblNume);
        panelSearch.add(txtNume);
        panelSearch.add(lblPrenume);
        panelSearch.add(txtPrenume);
        panelSearch.add(lblFunctie);
        panelSearch.add(txtFunctie);
        panelSearch.add(new JLabel()); // Spațiu gol
        panelSearch.add(btnCauta);

        // Tabel pentru afișarea rezultatelor
        String[] columnNames = {"ID", "Nume", "Prenume", "Funcție", "Tip Angajat", "Tip Medical"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Butoane pentru modificare și ștergere
        JPanel panelActions = new JPanel();
        JButton btnModifica = new JButton("Modifică");
        JButton btnSterge = new JButton("Șterge");
        JButton btnOrar = new JButton("Vezi Orar");
        panelActions.add(btnModifica);
        panelActions.add(btnSterge);
        panelActions.add(btnOrar);

        frame.add(panelSearch, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(panelActions, BorderLayout.SOUTH);

        frame.setVisible(true);

        // Acțiune pentru butonul de căutare
        btnCauta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nume = txtNume.getText().trim();
                String prenume = txtPrenume.getText().trim();
                String functie = txtFunctie.getText().trim();

                model.setRowCount(0); // Curățăm tabelul

                ResultSet rs = UtilizatorDAO.cautaAngajati(nume, prenume, functie);
                try {
                    while (rs != null && rs.next()) {
                        Vector<Object> row = new Vector<>();
                        row.add(rs.getInt("id"));
                        row.add(rs.getString("nume"));
                        row.add(rs.getString("prenume"));
                        row.add(rs.getString("functie"));
                        row.add(rs.getString("tip_angajat"));
                        row.add(rs.getString("tip_medical"));
                        model.addRow(row);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Eroare la căutare!", "Eroare", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Acțiune pentru butonul de modificare
        btnModifica.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(frame, "Selectați un angajat pentru modificare.", "Eroare", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Obținem modelul tabelului
                DefaultTableModel model = (DefaultTableModel) table.getModel();

                // Detectăm indexul coloanelor în mod dinamic
                int functieColumnIndex = table.getColumnModel().getColumnIndex("Funcție");
                int tipAngajatColumnIndex = table.getColumnModel().getColumnIndex("Tip Angajat");
                int tipMedicalColumnIndex = table.getColumnModel().getColumnIndex("Tip Medical");


                // Obține datele din tabel
                int id = (int) table.getValueAt(selectedRow, 0); // Presupunem că prima coloană este ID-ul
                String functie = (String) table.getValueAt(selectedRow, functieColumnIndex); // Indexul coloanei "functie"
                String tipAngajat = (String) table.getValueAt(selectedRow, tipAngajatColumnIndex); // Indexul coloanei "tip_angajat"
                String tipMedical = (String) table.getValueAt(selectedRow, tipMedicalColumnIndex); // Indexul coloanei "tip_medical"

                // Verificăm dacă este angajat medical (și poate edita doar tip_medical)
                if (!"medical".equals(tipAngajat)) {
                    tipMedical = null; // Dacă nu este medical, nu trebuie să aibă tip_medical
                }

                // Creăm câmpuri pentru editare
                JTextField tipAngajatField = new JTextField(tipAngajat);
                JTextField tipMedicalField = new JTextField(tipMedical);
                tipMedicalField.setEnabled("medical".equals(tipAngajat)); // Activează doar dacă este medical

                JPanel panel = new JPanel(new GridLayout(2, 2));
                panel.add(new JLabel("Tip Angajat:"));
                panel.add(tipAngajatField);
                panel.add(new JLabel("Tip Medical:"));
                panel.add(tipMedicalField);

                int result = JOptionPane.showConfirmDialog(frame, panel, "Modifică Angajat", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    String newTipAngajat = tipAngajatField.getText();
                    String newTipMedical = tipMedicalField.getText();

                    if (!"medical".equals(newTipAngajat)) {
                        newTipMedical = null; // Dacă nu mai este medical, eliminăm tipMedical
                    }

                    // Apelăm metoda de actualizare a bazei de date
                    boolean success = UtilizatorDAO.modificaAngajat(id, newTipAngajat, newTipMedical);
                    if (success) {
                        JOptionPane.showMessageDialog(frame, "Modificare efectuată cu succes.", "Succes", JOptionPane.INFORMATION_MESSAGE);
                        refreshTable(model); // Reîncarcă datele în tabel
                    } else {
                        JOptionPane.showMessageDialog(frame, "Eroare la modificare.", "Eroare", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Acțiune pentru butonul de ștergere
        btnSterge.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(frame, "Selectați un angajat pentru ștergere!", "Atenție", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int id = (int) model.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(frame, "Sigur doriți să ștergeți acest angajat?", "Confirmare", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    UtilizatorDAO.stergeAngajat(id);
                    btnCauta.doClick(); // Reîmprospătare tabel
                }
            }
        });

        btnOrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Verificăm dacă a fost selectat un angajat
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(frame, "Selectați un angajat pentru a vedea orarul.", "Eroare", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Obținem ID-ul angajatului din tabel
                // int idAngajat = (int) table.getValueAt(selectedRow, 0); // Presupunem că prima coloană este ID-ul
                int functieIdAngajat = table.getColumnModel().getColumnIndex("ID");
                int idAngajat = (int) table.getValueAt(selectedRow, functieIdAngajat);

                // Apelăm funcția care creează fereastra cu informațiile despre orar
                deschideFereastraOrar(idAngajat);
            }
        });

    }

    private static void refreshTable(DefaultTableModel model) {
        try {
            ResultSet rs = UtilizatorDAO.cautaAngajati("", "", ""); // Caută toți angajații

            // Obține modelul tabelului
            model.setRowCount(0); // Șterge toate rândurile existente

            while (rs.next()) {
                int id = rs.getInt("id");
                String nume = rs.getString("nume");
                String prenume = rs.getString("prenume");
                String functie = rs.getString("functie");
                String tipAngajat = rs.getString("tip_angajat");
                String tipMedical = rs.getString("tip_medical");

                model.addRow(new Object[]{id, nume, prenume, functie, tipAngajat, tipMedical});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deschideFereastraOrar(int idAngajat) {
        JFrame orarFrame = new JFrame("Orar Angajat");
        orarFrame.setSize(500, 400);
        orarFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Panou informații angajat
        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));

        ResultSet angajatResult = null;
        try {
            angajatResult = UtilizatorDAO.getAngajatInfo(idAngajat);
            if (angajatResult != null && angajatResult.next()) {
                String nume = angajatResult.getString("nume");
                String prenume = angajatResult.getString("prenume");
                int nrOre = angajatResult.getInt("nr_ore");

                JLabel lblInfo = new JLabel("Nume: " + nume + " " + prenume + " | Număr de ore: " + nrOre);
                lblInfo.setFont(new Font("Arial", Font.BOLD, 14));
                panelInfo.add(lblInfo);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try { if (angajatResult != null) angajatResult.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }

        // Panou orar generic
        JPanel panelOrarGeneric = new JPanel(new BorderLayout());
        String[] columnNames = {"Zi", "Început", "Sfârșit"};
        DefaultTableModel modelGeneric = new DefaultTableModel(columnNames, 0);
        ResultSet orarGenericResult = null;
        try {
            orarGenericResult = UtilizatorDAO.getOrarGeneric(idAngajat);
            while (orarGenericResult.next()) {
                modelGeneric.addRow(new Object[]{
                        orarGenericResult.getString("zi_saptamana"),
                        orarGenericResult.getString("ora_inceput"),
                        orarGenericResult.getString("ora_sfarsit")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try { if (orarGenericResult != null) orarGenericResult.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
        JTable tableGeneric = new JTable(modelGeneric);
        panelOrarGeneric.add(new JScrollPane(tableGeneric), BorderLayout.CENTER);

        // Panou orar specific
        JPanel panelOrarSpecific = new JPanel(new BorderLayout());
        DefaultTableModel modelSpecific = new DefaultTableModel(columnNames, 0);
        ResultSet orarSpecificResult = null;
        try {
            orarSpecificResult = UtilizatorDAO.getOrarSpecific(idAngajat);
            while (orarSpecificResult.next()) {
                modelSpecific.addRow(new Object[]{
                        orarSpecificResult.getString("data_calendaristica"),
                        orarSpecificResult.getString("ora_inceput"),
                        orarSpecificResult.getString("ora_sfarsit")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try { if (orarSpecificResult != null) orarSpecificResult.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
        JTable tableSpecific = new JTable(modelSpecific);
        panelOrarSpecific.add(new JScrollPane(tableSpecific), BorderLayout.CENTER);

        // Panou concedii
        JPanel panelConcedii = new JPanel();
        panelConcedii.setLayout(new BoxLayout(panelConcedii, BoxLayout.Y_AXIS));
        ResultSet concediuResult = null;
        try {
            concediuResult = UtilizatorDAO.getConcediu(idAngajat);
            while (concediuResult.next()) {
                JLabel lblConcediu = new JLabel(
                        concediuResult.getString("data_inceput") + " - " +
                                concediuResult.getString("data_sfarsit") + " | Motiv: " +
                                concediuResult.getString("motiv"));
                panelConcedii.add(lblConcediu);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try { if (concediuResult != null) concediuResult.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }

        tabbedPane.addTab("Informații", panelInfo);
        tabbedPane.addTab("Orar Generic", panelOrarGeneric);
        tabbedPane.addTab("Orar Specific", panelOrarSpecific);
        tabbedPane.addTab("Concedii", panelConcedii);

        orarFrame.add(tabbedPane);
        orarFrame.setVisible(true);
    }


    //============================================================================
    //===== Fereastra ECONOMIC
    //=============================================================================
    public static void fereastraEconomic() {
        JFrame frame = new JFrame("Modul Economic");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 600);

        // Crearea unui JTabbedPane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab-ul de cheltuieli
        JPanel cheltuieliPanel = new JPanel(new BorderLayout());
        JTable cheltuieliTable = new JTable();
        DefaultTableModel cheltuieliModel = new DefaultTableModel();

        // Setează coloanele conform tabelei `salariu`
        cheltuieliModel.setColumnIdentifiers(new Object[]{
                "ID Salariu", "ID Angajat", "Lună", "An", "Salariu Total",
                "Bonus din Servicii", "Nr. Ore Lucrate", "Salariu din Ore"
        });

        cheltuieliTable.setModel(cheltuieliModel);
        JScrollPane cheltuieliScrollPane = new JScrollPane(cheltuieliTable);
        cheltuieliPanel.add(cheltuieliScrollPane, BorderLayout.CENTER);

        // Buton pentru reîncărcarea datelor
        JButton btnRefresh = new JButton("Reîncarcă");
        btnRefresh.addActionListener(e -> actualizeazaSiIncarcaCheltuieli(cheltuieliModel));
        cheltuieliPanel.add(btnRefresh, BorderLayout.SOUTH);

        // Încarcă datele inițiale
        actualizeazaSiIncarcaCheltuieli(cheltuieliModel);

        tabbedPane.addTab("Cheltuieli", cheltuieliPanel);

        // Tab-ul de profituri (poate fi extins ulterior)
        JPanel profituriPanel = new JPanel(new BorderLayout());
        JLabel profituriLabel = new JLabel("Aici vor fi afișate informațiile despre profituri.", SwingConstants.CENTER);
        profituriPanel.add(profituriLabel, BorderLayout.CENTER);

        tabbedPane.addTab("Profituri", profituriPanel);

        // Adaugă tab-uri la fereastră
        frame.add(tabbedPane);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void actualizeazaSiIncarcaCheltuieli(DefaultTableModel cheltuieliModel) {
        try {
            // Obține cheltuielile din baza de date
            List<Object[]> cheltuieli = UtilizatorDAO.citesteCheltuieli();

            // Actualizează salariile pentru fiecare angajat
            for (Object[] row : cheltuieli) {
                int idAngajat = (int) row[1]; // ID Angajat este la indexul 1
                int luna = (int) row[2];      // Luna este la indexul 2
                int an = (int) row[3];        // Anul este la indexul 3
                UtilizatorDAO.actualizeazaSalariu(idAngajat, luna, an);
            }

            // Reîncarcă datele actualizate
            cheltuieli = UtilizatorDAO.citesteCheltuieli();

            // Golește tabelul și adaugă noile date
            cheltuieliModel.setRowCount(0);
            for (Object[] row : cheltuieli) {
                cheltuieliModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Eroare la încărcarea datelor: " + e.getMessage(),
                    "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }




    //============================================================================
    //===== Fereastra MEDICAL
    //=============================================================================

    public void fereastraMedical(String loginID) {
        UtilizatorDAO utilizatorDAO = new UtilizatorDAO();
        String rolMedical = utilizatorDAO.getMedicalRoleByLoginID(loginID);

        if (rolMedical == null) {
            JOptionPane.showMessageDialog(null, "Eroare: Rol medical inexistent!", "Eroare", JOptionPane.ERROR_MESSAGE);
            return;
        }

        switch (rolMedical.toLowerCase()) {
            case "receptionist":
                fereastraReceptionist(loginID);
                break;
            case "medic":
            case "asistent":
                fereastraMedicAsistent(loginID);
                break;
            default:
                JOptionPane.showMessageDialog(null, "Eroare: Rol necunoscut!", "Eroare", JOptionPane.ERROR_MESSAGE);
                break;
        }
    }

    public static void fereastraReceptionist(String loginID) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("Fereastra Receptionist");
        frame.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        ///////////////////////////////////////////////////////////////////////////////// Tab Creare Programare
        JPanel tabCreareProgramare = new JPanel(new BorderLayout());

        // Creare tabel pentru servicii
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Selectează", "ID Serviciu", "Denumire", "Preț", "Durată (minute)"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return (columnIndex == 0) ? Boolean.class : Object.class;
            }
        };

        JTable serviciiTable = new JTable(model);
        serviciiTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Ajustarea dimensiunilor coloanelor
        TableColumn selectColumn = serviciiTable.getColumnModel().getColumn(0);
        selectColumn.setMaxWidth(80);
        selectColumn.setMinWidth(80);

        JScrollPane scrollPane = new JScrollPane(serviciiTable);
        tabCreareProgramare.add(scrollPane, BorderLayout.CENTER);

        // Adăugare servicii în tabel din baza de date
        ResultSet resultSet = UtilizatorDAO.getServiciiDisponibile();
        try {
            while (resultSet != null && resultSet.next()) {
                model.addRow(new Object[]{false, resultSet.getInt("id_serviciu"),
                        resultSet.getString("denumire"),
                        resultSet.getInt("pret"),
                        resultSet.getInt("durata")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Câmpuri pentru introducerea altor informații
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        JTextField idClientField = new JTextField();
        JTextField idMedicField = new JTextField();
        JTextField dataConsultatieField = new JTextField();

        formPanel.add(new JLabel("ID Client:"));
        formPanel.add(idClientField);
        formPanel.add(new JLabel("ID Medic:"));
        formPanel.add(idMedicField);
        formPanel.add(new JLabel("Data Consultatie:"));
        formPanel.add(dataConsultatieField);

        tabCreareProgramare.add(formPanel, BorderLayout.NORTH);

        // Buton pentru crearea programării
        JButton submitProgramareButton = new JButton("Crează Programare");
        submitProgramareButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String idClient = idClientField.getText();
                String idMedic = idMedicField.getText();
                String dataConsultatie = dataConsultatieField.getText();

                ArrayList<Integer> serviciiSelectate = new ArrayList<>();
                for (int i = 0; i < serviciiTable.getRowCount(); i++) {
                    Boolean selected = (Boolean) serviciiTable.getValueAt(i, 0);
                    if (Boolean.TRUE.equals(selected)) {
                        serviciiSelectate.add((Integer) serviciiTable.getValueAt(i, 1));
                    }
                }

                UtilizatorDAO.creareProgramare(idClient, idMedic, dataConsultatie, serviciiSelectate);
            }
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(submitProgramareButton);
        tabCreareProgramare.add(bottomPanel, BorderLayout.SOUTH);

        ////////////////////////////////////////////////////////////////////////////////// mai sus tab-ul creare programare

        ////============================================================================tab inregistrare client
        //============================================================
        //=========================================

        JPanel tabInregistrarePacient = new JPanel();
        tabInregistrarePacient.setLayout(new BorderLayout());

        // Modelul tabelului pentru afișarea pacienților
        String[] columnNames = {"ID Client", "Nume", "Prenume", "CNP", "Data Nașterii", "Nr. Telefon"};
        DefaultTableModel modelPacienti = new DefaultTableModel(columnNames, 0);
        JTable tabelPacienti = new JTable(modelPacienti);
        JScrollPane scrollPanePacienti = new JScrollPane(tabelPacienti);

        // Formular pentru adăugare pacient
        JPanel formPanelClient = new JPanel(new GridLayout(6, 2));
        JTextField numeField = new JTextField();
        JTextField prenumeField = new JTextField();
        JTextField cnpField = new JTextField();
        JTextField dataNasteriiField = new JTextField();
        JTextField telefonField = new JTextField();
        JButton adaugaPacientButton = new JButton("Adaugă Pacient");

        // Adăugăm etichete și câmpuri în formular
        formPanelClient.add(new JLabel("Nume:"));
        formPanelClient.add(numeField);
        formPanelClient.add(new JLabel("Prenume:"));
        formPanelClient.add(prenumeField);
        formPanelClient.add(new JLabel("CNP:"));
        formPanelClient.add(cnpField);
        formPanelClient.add(new JLabel("Data Nașterii (YYYY-MM-DD):"));
        formPanelClient.add(dataNasteriiField);
        formPanelClient.add(new JLabel("Nr. Telefon:"));
        formPanelClient.add(telefonField);
        formPanelClient.add(adaugaPacientButton);

        // Adăugăm componentele în tab
        tabInregistrarePacient.add(scrollPanePacienti, BorderLayout.CENTER);
        tabInregistrarePacient.add(formPanelClient, BorderLayout.SOUTH);

        // Eveniment pentru butonul "Adaugă Pacient"
        adaugaPacientButton.addActionListener(e -> {
            String nume = numeField.getText();
            String prenume = prenumeField.getText();
            String cnp = cnpField.getText();
            String dataNasterii = dataNasteriiField.getText();
            String telefon = telefonField.getText();

            if (nume.isEmpty() || prenume.isEmpty() || cnp.isEmpty() || dataNasterii.isEmpty() || telefon.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Toate câmpurile trebuie completate!", "Eroare", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Apelăm metoda DAO pentru a adăuga pacientul (de implementat ulterior)
            UtilizatorDAO.adaugaPacient(nume, prenume, cnp, dataNasterii, telefon);

            // Adăugăm pacientul și în tabel (temporar, până la refresh din DB)
            modelPacienti.addRow(new Object[]{null, nume, prenume, cnp, dataNasterii, telefon});

            // Curățăm câmpurile după adăugare
            numeField.setText("");
            prenumeField.setText("");
            cnpField.setText("");
            dataNasteriiField.setText("");
            telefonField.setText("");
        });

        // Inițializăm tabelul cu date din DB (de implementat în DAO)
        List<String[]> pacienti = UtilizatorDAO.getTotiPacientii();
        for (String[] pacient : pacienti) {
            modelPacienti.addRow(pacient);
        }

        //=========================================
        //============================================================
        ////============================================================================ sfarsit tab inregistrare client

        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ Tab pentru emitere bon
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        //+++++++++++++++++++++++++++++++++++++++++++

        JPanel tabEmitereBon = new JPanel();
        tabEmitereBon.setLayout(new BorderLayout());

// Crearea tabelului pentru bonuri
        String[] columnNamesConsultatie = {"ID Consultatie", "Nume Client", "Prenume Client", "Data Consultatie"};
        DefaultTableModel modelBonuri = new DefaultTableModel(columnNamesConsultatie, 0);

// Crearea JTable pentru bonuri
        JTable bonuriTable = new JTable(modelBonuri);
        bonuriTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPaneConsultatie = new JScrollPane(bonuriTable);

        // Adăugăm tabelul în tab
        tabEmitereBon.add(scrollPaneConsultatie, BorderLayout.CENTER);

        // Funcție pentru încărcarea bonurilor din baza de date
        ResultSet resultSetBonuri = UtilizatorDAO.getBonuri(); // Vei implementa această funcție mai târziu
        try {
            while (resultSet != null && resultSetBonuri.next()) {
                int idConsultatie = resultSetBonuri.getInt("id_consultatie");
                String numeClient = resultSetBonuri.getString("nume");
                String prenumeClient = resultSetBonuri.getString("prenume");
                Date dataConsultatie = resultSetBonuri.getDate("data_consultatie");

                // Adăugăm rânduri în tabel
                modelBonuri.addRow(new Object[]{idConsultatie, numeClient, prenumeClient, dataConsultatie});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Apelăm funcția pentru a încărca bonurile când se deschide tab-ul

        // Adăugăm un listener pentru a detecta selecția unui bon
        // List selection listener pentru bonuriTable
        bonuriTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = bonuriTable.getSelectedRow();
            if (selectedRow != -1) {
                int idConsultatie = (int) bonuriTable.getValueAt(selectedRow, 0);
                // Apelează funcția care va afișa bonul, folosind id-ul consultatiei
                //afiseazaBon(idConsultatie);
                //nevermind, mai bine fac cand apas butonul dar las si codul acesta just in case
            }
        });

        // Buton pentru emitere bon
        JButton emitereBonButton = new JButton("Emitere Bon");
        emitereBonButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = bonuriTable.getSelectedRow();
                if (selectedRow != -1) {
                    // Preluăm ID-ul consultației selectate din tabel
                    int idConsultatie = (int) bonuriTable.getValueAt(selectedRow, 0);
                    // Apelăm funcția care va emite bonul pentru consultația respectivă
                    afiseazaBon(idConsultatie);
                } else {
                    // Dacă nu este selectată nicio consultație, putem adăuga o fereastră de alertă
                    JOptionPane.showMessageDialog(null, "Vă rugăm să selectați o consultație din tabel pentru a emite bonul.", "Eroare", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        // Panou pentru butoane
        JPanel bottomPanelEmitereBon = new JPanel();
        bottomPanelEmitereBon.add(emitereBonButton);
        tabEmitereBon.add(bottomPanelEmitereBon, BorderLayout.SOUTH);


        //+++++++++++++++++++++++++++++++++++++++++++
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        // Adăugare tab-uri în tabbedPane
        tabbedPane.addTab("Creare Programare", tabCreareProgramare);
        tabbedPane.addTab("Înregistrare Pacient", tabInregistrarePacient);
        tabbedPane.addTab("Emitere Bon", tabEmitereBon);

        frame.add(tabbedPane);
        frame.setVisible(true);
    }

    public static void afiseazaBon(int idConsultatie) {
        JFrame bonFrame = new JFrame("Bon Consultație");
        bonFrame.setSize(450, 550);
        bonFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        bonFrame.setLayout(new BorderLayout(10, 10));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;

        Map<String, JLabel> labels = new HashMap<>();
        String[] fieldNames = {
                "Nume Client", "Prenume Client", "Preț", "Nume Medic", "Prenume Medic",
                "Data Consultației", "Durata (minute)", "Unitate"
        };

        for (String field : fieldNames) {
            JLabel label = new JLabel(field + ": ");
            label.setFont(new Font("Arial", Font.BOLD, 14));
            JLabel valueLabel = new JLabel("");
            valueLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            labels.put(field, valueLabel);

            panel.add(label, gbc);
            gbc.gridx++;
            panel.add(valueLabel, gbc);
            gbc.gridx = 0;
            gbc.gridy++;
        }

        JLabel serviciiLabel = new JLabel("Servicii:");
        serviciiLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JTextArea serviciiTextArea = new JTextArea(10, 30);
        serviciiTextArea.setFont(new Font("Arial", Font.PLAIN, 14));
        serviciiTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(serviciiTextArea);

        gbc.gridwidth = 2;
        panel.add(serviciiLabel, gbc);
        gbc.gridy++;
        panel.add(scrollPane, gbc);

        bonFrame.add(panel, BorderLayout.CENTER);
        bonFrame.setVisible(true);

        // Obține datele consultației
        List<Map<String, String>> listaConsultatii = UtilizatorDAO.getDetailsForConsultatie(idConsultatie);

        if (!listaConsultatii.isEmpty()) {
            SwingUtilities.invokeLater(() -> {
                Set<String> serviciiUnice = new HashSet<>();
                StringBuilder serviciiText = new StringBuilder();
                double totalPret = 0;

                for (Map<String, String> consultatie : listaConsultatii) {
                    labels.get("Nume Client").setText(consultatie.get("Nume Client"));
                    labels.get("Prenume Client").setText(consultatie.get("Prenume Client"));
                    labels.get("Nume Medic").setText(consultatie.get("Nume Medic"));
                    labels.get("Prenume Medic").setText(consultatie.get("Prenume Medic"));
                    labels.get("Data Consultației").setText(consultatie.get("Data Consultației"));
                    labels.get("Durata (minute)").setText(consultatie.get("Durata (minute)"));
                    labels.get("Unitate").setText(consultatie.get("Unitate"));

                    String serviciu = consultatie.get("Serviciu");
                    String pretServiciuStr = consultatie.get("Preț Serviciu");

                    if (!serviciiUnice.contains(serviciu)) {
                        serviciiUnice.add(serviciu);
                        double pretServiciu = Double.parseDouble(pretServiciuStr.replace(",", "."));

                        serviciiText.append(serviciu).append(" - ").append(String.format("%.2f", pretServiciu)).append(" RON\n");
                        totalPret += pretServiciu;
                    }
                }

                serviciiTextArea.setText(serviciiText.toString());
                labels.get("Preț").setText(String.format("%.2f", totalPret) + " RON");
            });
        }
    }




    public void fereastraMedicAsistent(String loginID) {
        JFrame frame = new JFrame("Fereastra Medic Asistent");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel panelProgramari = new JPanel(new BorderLayout());
        // Tab-ul Programari

        String[] columnNamesProgramari = {"ID Consultatie", "ID Pacient", "Nume Pacient", "Prenume Pacient", "Nume Medic", "Prenume Medic", "Data Consultatiei", "Servicii"};
        DefaultTableModel modelProgramari = new DefaultTableModel(null, columnNamesProgramari);
        JTable tableProgramari = new JTable(modelProgramari);
        JScrollPane scrollPaneProgramari = new JScrollPane(tableProgramari);
        panelProgramari.add(scrollPaneProgramari, BorderLayout.CENTER);

        // Buton pentru întocmirea raportului
        JButton btnIntocmireRaport = new JButton("Întocmire Raport");
        panelProgramari.add(btnIntocmireRaport, BorderLayout.SOUTH);

        // Popularea tabelei cu datele din consultatie
        ResultSet resultSet = UtilizatorDAO.getConsultatieDetails();
        try {
            while (resultSet.next()) {
                // Extragem datele din ResultSet
                int idConsultatie = resultSet.getInt("id_consultatie");
                int idPacient = resultSet.getInt("id_client");
                String numePacient = resultSet.getString("client_nume");
                String prenumePacient = resultSet.getString("client_prenume");
                String numeMedic = resultSet.getString("medic_nume");
                String prenumeMedic = resultSet.getString("medic_prenume");
                Date dataConsultatie = resultSet.getDate("data_consultatie");
                String servicii = resultSet.getString("servicii");

                // Adăugăm rândul în model
                modelProgramari.addRow(new Object[]{
                        idConsultatie, idPacient, numePacient, prenumePacient, numeMedic, prenumeMedic, dataConsultatie, servicii
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Acțiunea butonului de întocmire raport
        btnIntocmireRaport.addActionListener(e -> {
            int selectedRow = tableProgramari.getSelectedRow();
            if (selectedRow != -1) {
                // Extragem ID-ul consultatiei din coloana corespunzătoare (prima coloană)
                String consultatieID = modelProgramari.getValueAt(selectedRow, 0).toString();
                // Apelăm funcția de întocmire raport cu ID-ul consultatiei
                intocmireRaport(consultatieID);
            } else {
                JOptionPane.showMessageDialog(frame, "Selectați o consultatie pentru a întocmi raportul.");
            }
        });

        // Tab-ul Istoric
        // Tab-ul Istoric
        JPanel panelIstoric = new JPanel(new BorderLayout());
        String[] columnNames = {
                "ID Consultatie", "Nume Analiza", "Rezultat Analiza", "ID Medic Recomandator",
                "ID Asistent", "Istoric", "Simptome", "Investigatii", "Diagnostic",
                "Recomandari", "Nume Pacient", "Nume Medic", "Servicii Oferite"
        };
        DefaultTableModel modelIstoric = new DefaultTableModel(null, columnNames);
        JTable tableIstoric = new JTable(modelIstoric);
        JScrollPane scrollPaneIstoric = new JScrollPane(tableIstoric);
        JButton btnModificareRaport = new JButton("Modificare Raport");
        JButton btnParafare = new JButton("Parafare Raport");

// Adăugăm acțiuni pentru butoane
        btnModificareRaport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tableIstoric.getSelectedRow();
                if (selectedRow != -1) {
                    String raportID = modelIstoric.getValueAt(selectedRow, 0).toString();
                    // Apel către funcția de modificare raport
                    modificareRaport(raportID);
                } else {
                    JOptionPane.showMessageDialog(panelIstoric, "Selectați un raport pentru modificare.");
                }
            }
        });

        btnParafare.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tableIstoric.getSelectedRow();
                if (selectedRow != -1) {
                    String raportID = modelIstoric.getValueAt(selectedRow, 0).toString();
                    String tipMedical = UtilizatorDAO.getMedicalRoleByLoginID(loginID);  // Acesta ar trebui să fie obținut din contextul aplicației

                    // Verificăm dacă utilizatorul are rolul de medic
                    if (tipMedical.equals("medic")) {
                        // Apel către funcția de parafare raport
                        parafareRaport(raportID);
                    } else {
                        JOptionPane.showMessageDialog(panelIstoric, "Doar medicii pot parafa rapoartele.");
                    }
                } else {
                    JOptionPane.showMessageDialog(panelIstoric, "Selectați un raport pentru parafare.");
                }
            }
        });

// Panoul pentru butoane
        JPanel panelButoaneIstoric = new JPanel();
        panelButoaneIstoric.add(btnModificareRaport);
        panelButoaneIstoric.add(btnParafare);

        panelIstoric.add(scrollPaneIstoric, BorderLayout.CENTER);
        panelIstoric.add(panelButoaneIstoric, BorderLayout.SOUTH);

// Încarcă datele rapoartelor în tabel
        incarcareRapoarte(modelIstoric);


        //final cod pt tabul istoric
        tabbedPane.addTab("Programări", panelProgramari);
        tabbedPane.addTab("Istoric", panelIstoric);

        frame.add(tabbedPane);
        frame.setVisible(true);
    }

    private static void intocmireRaport(String consultatieID) {
        // Creați un JDialog pentru a deschide fereastra
        JDialog raportDialog = new JDialog();
        raportDialog.setTitle("Întocmire Raport Consultatie");
        raportDialog.setSize(600, 700);
        raportDialog.setLocationRelativeTo(null); // Centrează fereastra pe ecran
        raportDialog.setLayout(new GridBagLayout()); // Folosim GridBagLayout pentru plasarea mai flexibilă a componentelor
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Adăugăm margini între componente
        gbc.anchor = GridBagConstraints.WEST; // Alinierea la stânga pentru etichete

        // Fonturi pentru un aspect mai plăcut
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 12);

        // Câmpuri pentru introducerea datelor
        JLabel lblConsultatieID = new JLabel("ID Consultatie:");
        lblConsultatieID.setFont(labelFont);
        JTextField txtConsultatieID = new JTextField(20);
        txtConsultatieID.setText(consultatieID);
        txtConsultatieID.setEditable(false);
        txtConsultatieID.setFont(fieldFont);
        txtConsultatieID.setBackground(Color.LIGHT_GRAY);

        JLabel lblIdPacient = new JLabel("ID Pacient:");
        lblIdPacient.setFont(labelFont);
        JTextField txtIdPacient = new JTextField(20);
        txtIdPacient.setFont(fieldFont);

        JLabel lblIdMedic = new JLabel("ID Medic:");
        lblIdMedic.setFont(labelFont);
        JTextField txtIdMedic = new JTextField(20);
        txtIdMedic.setFont(fieldFont);

        JLabel lblNumeAnaliza = new JLabel("Nume Analiza:");
        lblNumeAnaliza.setFont(labelFont);
        JTextField txtNumeAnaliza = new JTextField(20);
        txtNumeAnaliza.setFont(fieldFont);

        JLabel lblRezultatAnaliza = new JLabel("Rezultatul Analizei:");
        lblRezultatAnaliza.setFont(labelFont);
        JTextField txtRezultatAnaliza = new JTextField(20);
        txtRezultatAnaliza.setFont(fieldFont);

        JLabel lblMedicRecomandator = new JLabel("ID Medic Recomandator:");
        lblMedicRecomandator.setFont(labelFont);
        JTextField txtMedicRecomandator = new JTextField(20);
        txtMedicRecomandator.setFont(fieldFont);

        JLabel lblAsistent = new JLabel("ID Asistent:");
        lblAsistent.setFont(labelFont);
        JTextField txtAsistent = new JTextField(20);
        txtAsistent.setFont(fieldFont);

        JLabel lblIstoric = new JLabel("Istoric:");
        lblIstoric.setFont(labelFont);
        JTextArea txtIstoric = new JTextArea(4, 20);
        txtIstoric.setFont(fieldFont);
        txtIstoric.setLineWrap(true);
        txtIstoric.setWrapStyleWord(true);
        JScrollPane scrollIstoric = new JScrollPane(txtIstoric);

        JLabel lblSimptome = new JLabel("Simptome:");
        lblSimptome.setFont(labelFont);
        JTextField txtSimptome = new JTextField(20);
        txtSimptome.setFont(fieldFont);

        JLabel lblInvestigatii = new JLabel("Investigatii:");
        lblInvestigatii.setFont(labelFont);
        JTextField txtInvestigatii = new JTextField(20);
        txtInvestigatii.setFont(fieldFont);

        JLabel lblDiagnostic = new JLabel("Diagnostic:");
        lblDiagnostic.setFont(labelFont);
        JTextField txtDiagnostic = new JTextField(20);
        txtDiagnostic.setFont(fieldFont);

        JLabel lblEcomandati = new JLabel("Recomandari:");
        lblEcomandati.setFont(labelFont);
        JTextField txtEcomandati = new JTextField(20);
        txtEcomandati.setFont(fieldFont);

        // Buton pentru trimiterea raportului
        JButton btnTrimiteRaport = new JButton("Trimite Raport");
        btnTrimiteRaport.setFont(new Font("Arial", Font.BOLD, 14));
        btnTrimiteRaport.setBackground(new Color(34, 193, 195));
        btnTrimiteRaport.setForeground(Color.WHITE);

        // Adăugăm componentele în fereastră folosind GridBagLayout
        gbc.gridx = 0;
        gbc.gridy = 0;
        raportDialog.add(lblConsultatieID, gbc);
        gbc.gridx = 1;
        raportDialog.add(txtConsultatieID, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        raportDialog.add(lblIdPacient, gbc);
        gbc.gridx = 1;
        raportDialog.add(txtIdPacient, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        raportDialog.add(lblIdMedic, gbc);
        gbc.gridx = 1;
        raportDialog.add(txtIdMedic, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        raportDialog.add(lblNumeAnaliza, gbc);
        gbc.gridx = 1;
        raportDialog.add(txtNumeAnaliza, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        raportDialog.add(lblRezultatAnaliza, gbc);
        gbc.gridx = 1;
        raportDialog.add(txtRezultatAnaliza, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        raportDialog.add(lblMedicRecomandator, gbc);
        gbc.gridx = 1;
        raportDialog.add(txtMedicRecomandator, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        raportDialog.add(lblAsistent, gbc);
        gbc.gridx = 1;
        raportDialog.add(txtAsistent, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        raportDialog.add(lblIstoric, gbc);
        gbc.gridx = 1;
        gbc.gridheight = 2;
        raportDialog.add(scrollIstoric, gbc);
        gbc.gridheight = 1;

        gbc.gridx = 0;
        gbc.gridy = 9;
        raportDialog.add(lblSimptome, gbc);
        gbc.gridx = 1;
        raportDialog.add(txtSimptome, gbc);

        gbc.gridx = 0;
        gbc.gridy = 10;
        raportDialog.add(lblInvestigatii, gbc);
        gbc.gridx = 1;
        raportDialog.add(txtInvestigatii, gbc);

        gbc.gridx = 0;
        gbc.gridy = 11;
        raportDialog.add(lblDiagnostic, gbc);
        gbc.gridx = 1;
        raportDialog.add(txtDiagnostic, gbc);

        gbc.gridx = 0;
        gbc.gridy = 12;
        raportDialog.add(lblEcomandati, gbc);
        gbc.gridx = 1;
        raportDialog.add(txtEcomandati, gbc);

        gbc.gridx = 1;
        gbc.gridy = 13;
        raportDialog.add(btnTrimiteRaport, gbc);

        // Acțiune la apăsarea butonului "Trimite Raport"
        btnTrimiteRaport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Extragem datele din câmpuri
                String pacientID = txtIdPacient.getText();
                String medicID = txtIdMedic.getText();
                String numeAnaliza = txtNumeAnaliza.getText();
                String rezultatAnaliza = txtRezultatAnaliza.getText();
                String medicRecomandator = txtMedicRecomandator.getText();
                String asistent = txtAsistent.getText();
                String istoric = txtIstoric.getText();
                String simptome = txtSimptome.getText();
                String investigatii = txtInvestigatii.getText();
                String diagnostic = txtDiagnostic.getText();
                String ecomandati = txtEcomandati.getText();

                // Apelăm funcția pentru a salva datele în baza de date
                // Va trebui să definim funcțiile respective în UtilizatorDAO pentru salvarea datelor
                UtilizatorDAO.salvareRaport(consultatieID, pacientID, medicID, numeAnaliza, rezultatAnaliza, medicRecomandator, asistent, istoric, simptome, investigatii, diagnostic, ecomandati);

                // Închidem fereastra după trimiterea raportului
                JOptionPane.showMessageDialog(raportDialog, "Raportul a fost trimis!");
                raportDialog.dispose(); // Închide fereastra
            }
        });

        // Setăm vizibilitatea ferestrei
        raportDialog.setVisible(true);
    }

    public static void incarcareRapoarte(DefaultTableModel model) {
        try {
            // Apelăm funcția statică din UtilizatorDAO pentru a obține ResultSet-ul cu datele
            ResultSet rs = UtilizatorDAO.getRaportData();

            while (rs.next()) {
                // Definim un array de obiecte cu 13 coloane
                Object[] row = new Object[13]; // Acum avem 13 coloane de afișat

                // Obținem datele din ResultSet și le punem în array-ul 'row'
                row[0] = rs.getInt("id_consultatie");
                row[1] = rs.getString("nume_analiza");
                row[2] = rs.getString("rezultat_analiza");
                row[3] = rs.getInt("id_medic_recomandator");
                row[4] = rs.getInt("id_asistent");
                row[5] = rs.getString("istoric");
                row[6] = rs.getString("simptome");
                row[7] = rs.getString("investigatii");
                row[8] = rs.getString("diagnostic");
                row[9] = rs.getString("recomandari");
                row[10] = rs.getString("nume_pacient") + " " + rs.getString("prenume_pacient");
                row[11] = rs.getString("nume_medic") + " " + rs.getString("prenume_medic");

                // Adăugăm serviciile oferite într-o coloană separată
                String serviciiOferite = rs.getString("servicii_oferite");
                if (serviciiOferite != null && !serviciiOferite.isEmpty()) {
                    row[12] = serviciiOferite;
                } else {
                    row[12] = "Nu există servicii";
                }

                // Adăugăm linia în tabel
                model.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Eroare la încărcarea rapoartelor: " + e.getMessage());
        }
    }

    private void modificareRaport(String raportID) {
        System.out.println("Modificare raport ID: " + raportID);
        JOptionPane.showMessageDialog(null, "Datele sunt actuale","Modificare", JOptionPane.INFORMATION_MESSAGE);
    }
    private void parafareRaport(String raportID) {
        JOptionPane.showMessageDialog(null, "Raportul cu ID-ul " + raportID + " a fost parafat cu succes!", "Parafare Succes", JOptionPane.INFORMATION_MESSAGE);
    }






}
