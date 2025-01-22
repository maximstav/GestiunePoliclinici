/*
Proiect la disciplina: Baza de Date
Titlu: Sistem Informatizat de Gestiune a unui Lanț de Policlinici
Realizat de: Staver Maxim, grupa 30223
*/
// Acest fisier contine
// clasa Utilizator Data Access Object (DAO)
// responsabila pentru gestionarea tuturor
// interactiunilor cu baza de date, cum ar fi citirea,
// scrierea, actualizarea si stergerea datelor

import javax.swing.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.*;
import java.util.*;

public class UtilizatorDAO {

    public static String verificaAutentificare(String loginID, String parola) {
        String sql = "SELECT functie FROM utilizator WHERE loginID = ? AND parola = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, loginID);
            stmt.setString(2, parola);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("functie"); // Returnează tipul utilizatorului
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Dacă nu găsește utilizatorul, returnează null
    }

    public static boolean esteAdministrator(String loginID) {
        return verificaRolUtilizator("esteAdministrator", loginID);
    }
    public static boolean esteSuperAdministrator(String loginID) {
        return verificaRolUtilizator("esteSuperAdministrator", loginID);
    }
    public static boolean esteAngajat(String loginID) {
        return verificaRolUtilizator("esteAngajat", loginID);
    }

    private static boolean verificaRolUtilizator(String functieSQL, String loginID) {
        String sql = "SELECT " + functieSQL + "(?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, loginID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getBoolean(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String obtineTipAngajat(String loginID) {
        String sql = "SELECT a.tip_angajat FROM utilizator u " +
                "JOIN angajat a ON u.id = a.id " +
                "WHERE u.loginID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, loginID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("tip_angajat"); // Returnează tipul de angajat (resurse-umane, economic, medical)
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Dacă nu se găsește utilizatorul sau altă eroare
    }

    public static Map<String, String> getInfoAngajat(String loginID) {
        Map<String, String> infoAngajat = new HashMap<>();
        String query = "SELECT u.nume, u.prenume, u.adresa, u.nrtel, u.email, u.IBAN, " +
                "u.nrcontact, u.data_angajarii, u.functie, a.salariu_pe_ora, a.nr_ore, a.tip_angajat " +
                "FROM utilizator u " +
                "JOIN angajat a ON u.id = a.id " +
                "WHERE u.loginID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, loginID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                infoAngajat.put("Nume", rs.getString("nume"));
                infoAngajat.put("Prenume", rs.getString("prenume"));
                infoAngajat.put("Adresă", rs.getString("adresa"));
                infoAngajat.put("Nr. Tel", rs.getString("nrtel"));
                infoAngajat.put("Email", rs.getString("email"));
                infoAngajat.put("IBAN", rs.getString("IBAN"));
                infoAngajat.put("Nr. Contact", rs.getString("nrcontact"));
                infoAngajat.put("Data Angajării", rs.getString("data_angajarii"));
                infoAngajat.put("Funcție", rs.getString("functie"));
                infoAngajat.put("Salariu pe Oră", String.valueOf(rs.getInt("salariu_pe_ora")));
                infoAngajat.put("Nr. Ore", String.valueOf(rs.getInt("nr_ore")));
                infoAngajat.put("Tip Angajat", rs.getString("tip_angajat"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return infoAngajat;
    }

    //==================================================== Modulele Administrator si Super Admin

    // Adaugă un utilizator în baza de date
    public static boolean adaugaUtilizator(String CNP, String loginID, String parola, String nume, String prenume, String adresa, String nrtel, String email, String IBAN, String functie, String nrcontact) {
        // Modificăm SQL-ul pentru a include și data_angajarii
        String sql = "INSERT INTO utilizator (CNP, loginID, parola, nume, prenume, adresa, nrtel, email, IBAN, nrcontact, functie, data_angajarii) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, CNP);
            stmt.setString(2, loginID);
            stmt.setString(3, parola);
            stmt.setString(4, nume);
            stmt.setString(5, prenume);
            stmt.setString(6, adresa);
            stmt.setString(7, nrtel);
            stmt.setString(8, email);
            stmt.setString(9, IBAN);
            stmt.setString(10, nrcontact);
            stmt.setString(11, functie);

            // Adaugă data curentă pentru câmpul data_angajarii
            stmt.setDate(12, new java.sql.Date(System.currentTimeMillis())); // data curentă

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    // Actualizează un utilizator în baza de datepentru admin
    public static boolean actualizeazaUtilizator(int id, String CNP, String loginID, String parola, String nume, String prenume, String adresa, String nrtel, String email, String IBAN, String functie, String nrContact) {
        String sql = "UPDATE utilizator SET CNP = ?, loginID = ?, parola = ?, nume = ?, prenume = ?, adresa = ?, nrtel = ?, email = ?, IBAN = ?, functie = ?, nrcontact = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, CNP);
            stmt.setString(2, loginID);
            stmt.setString(3, parola);
            stmt.setString(4, nume);
            stmt.setString(5, prenume);
            stmt.setString(6, adresa);
            stmt.setString(7, nrtel);
            stmt.setString(8, email);
            stmt.setString(9, IBAN);
            stmt.setString(10, functie);
            stmt.setString(11, nrContact);
            stmt.setInt(12, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    //asta de mai jos e pentru super-admin
    public static boolean actualizeazaUtilizator(int id, String CNP, String loginID, String parola, String nume, String prenume, String adresa, String nrtel, String email, String IBAN, String functie, String nrContact, String rolUtilizator) {
        // Adăugăm rolul utilizatorului în interogarea SQL
        String sql = "UPDATE utilizator SET CNP = ?, loginID = ?, parola = ?, nume = ?, prenume = ?, adresa = ?, nrtel = ?, email = ?, IBAN = ?, functie = ?, nrcontact = ?, rol = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Setăm valorile pentru fiecare parametru, inclusiv rolul
            stmt.setString(1, CNP);
            stmt.setString(2, loginID);
            stmt.setString(3, parola);
            stmt.setString(4, nume);
            stmt.setString(5, prenume);
            stmt.setString(6, adresa);
            stmt.setString(7, nrtel);
            stmt.setString(8, email);
            stmt.setString(9, IBAN);
            stmt.setString(10, functie);
            stmt.setString(11, nrContact);
            stmt.setString(12, rolUtilizator);  // Setăm rolul utilizatorului
            stmt.setInt(13, id);  // Setăm ID-ul utilizatorului

            // Executăm actualizarea
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    // Șterge un utilizator din baza de date
    public static boolean stergeUtilizator(int id) {
        String sql = "DELETE FROM utilizator WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Încărcarea datelor utilizatorilor
    public static ResultSet incarcaUtilizatori() {
        String sql = "SELECT * FROM utilizator";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }


    //=============================================================
    // ======== Modulul resurse umane
    //=============================================================
    public static ResultSet cautaAngajati(String nume, String prenume, String functie) {
        String sql = "SELECT u.*, a.tip_angajat, m.tip_medical " +
                "FROM utilizator u " +
                "LEFT JOIN angajat a ON u.id = a.id " +
                "LEFT JOIN medical m ON a.id = m.id " +
                "WHERE u.functie NOT IN ('Administrator', 'Super-Administrator') " + // Excludem administratorii
                "AND (u.nume LIKE ? OR ? IS NULL OR ? = '') " +
                "AND (u.prenume LIKE ? OR ? IS NULL OR ? = '') " +
                "AND (u.functie LIKE ? OR a.tip_angajat LIKE ? OR m.tip_medical LIKE ? " +
                "     OR ? IS NULL OR ? = '')";

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);

            // Setăm parametrii pentru LIKE (căutare parțială)
            stmt.setString(1, "%" + nume + "%");
            stmt.setString(2, nume);
            stmt.setString(3, nume);

            stmt.setString(4, "%" + prenume + "%");
            stmt.setString(5, prenume);
            stmt.setString(6, prenume);

            stmt.setString(7, "%" + functie + "%");
            stmt.setString(8, "%" + functie + "%");
            stmt.setString(9, "%" + functie + "%");
            stmt.setString(10, functie);
            stmt.setString(11, functie);

            return stmt.executeQuery(); // Returnează ResultSet-ul pentru prelucrare ulterioară

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // În caz de eroare, returnează null
    }

    public static boolean modificaAngajat(int id, String tipAngajat, String tipMedical) {
        String sqlAngajat = "UPDATE angajat SET tip_angajat = ? WHERE id = ?";
        String sqlMedical = "UPDATE medical SET tip_medical = ? WHERE id = ?";

        try {
            Connection conn = DatabaseConnection.getConnection();
            // Actualizăm tip_angajat în tabelul angajat
            PreparedStatement stmtAngajat = conn.prepareStatement(sqlAngajat);
            stmtAngajat.setString(1, tipAngajat);
            stmtAngajat.setInt(2, id);
            stmtAngajat.executeUpdate();
            stmtAngajat.close();

            // Dacă angajatul este medical, actualizăm și tip_medical
            if ("medical".equals(tipAngajat)) {
                PreparedStatement stmtMedical = conn.prepareStatement(sqlMedical);
                stmtMedical.setString(1, tipMedical);
                stmtMedical.setInt(2, id);
                stmtMedical.executeUpdate();
                stmtMedical.close();
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void stergeAngajat(int id) {
        String sql = "DELETE FROM utilizator WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Obține informațiile despre angajat (nume, prenume, nr ore)
    public static ResultSet getAngajatInfo(int idAngajat) throws SQLException {
        String query = "SELECT u.nume, u.prenume, a.nr_ore FROM utilizator u JOIN angajat a ON u.id = a.id WHERE a.id = ?";
        PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(query);
        stmt.setInt(1, idAngajat);  // Setăm id-ul angajatului
        return stmt.executeQuery();
    }

    // Obține orarul generic al angajatului
    public static ResultSet getOrarGeneric(int idAngajat) throws SQLException {
        String query = "SELECT zi_saptamana, ora_inceput, ora_sfarsit FROM orar_generic " +
                "WHERE id_angajat = ?";
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        try {
            stmt = DatabaseConnection.getConnection().prepareStatement(query);
            stmt.setInt(1, idAngajat);
            resultSet = stmt.executeQuery();
            return resultSet;
        } catch (SQLException e) {
            throw e;
        } finally {
            // Nu închidem resursele aici, pentru că le folosim în altă parte
        }
    }

    // Obține orarul specific al angajatului
    public static ResultSet getOrarSpecific(int idAngajat) throws SQLException {
        String query = "SELECT data_calendaristica, ora_inceput, ora_sfarsit FROM orar_specific " +
                "WHERE id_angajat = ?";
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        try {
            stmt = DatabaseConnection.getConnection().prepareStatement(query);
            stmt.setInt(1, idAngajat);
            resultSet = stmt.executeQuery();
            return resultSet;
        } catch (SQLException e) {
            throw e;
        } finally {
            // Nu închidem resursele aici, pentru că le folosim în altă parte
        }
    }

    // Obține concediile angajatului
    public static ResultSet getConcediu(int idAngajat) throws SQLException {
        String query = "SELECT data_inceput, data_sfarsit, motiv FROM concediu " +
                "WHERE id_angajat = ?";
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        try {
            stmt = DatabaseConnection.getConnection().prepareStatement(query);
            stmt.setInt(1, idAngajat);
            resultSet = stmt.executeQuery();
            return resultSet;
        } catch (SQLException e) {
            throw e;
        } finally {
            // Nu închidem resursele aici, pentru că le folosim în altă parte
        }
    }


    //=============================================================
    // ======== Modulul economic
    //=============================================================

    public static void actualizeazaSalariu(int idAngajat, int luna, int an) {
        String queryAngajat = "SELECT salariu_pe_ora, nr_ore, tip_angajat FROM angajat WHERE id = ?";
        String queryMedic = "SELECT procent_servicii FROM medic WHERE id = ?";
        String queryConsultatii = "SELECT id_consultatie FROM consultatie WHERE id_medic = ?";
        String queryServicii = "SELECT SUM(s.pret) FROM consultatie_servicii cs JOIN serviciu s ON cs.id_serviciu = s.id_serviciu WHERE cs.id_consultatie = ?";
        String insertOrUpdateSalariu = "INSERT INTO salariu (id_angajat, luna, an, salariu_total, bonus_din_servicii, nr_ore_lucrate, salariu_din_ore) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE salariu_total = VALUES(salariu_total), " +
                "bonus_din_servicii = VALUES(bonus_din_servicii), nr_ore_lucrate = VALUES(nr_ore_lucrate), salariu_din_ore = VALUES(salariu_din_ore)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmtAngajat = conn.prepareStatement(queryAngajat);
             PreparedStatement stmtMedic = conn.prepareStatement(queryMedic);
             PreparedStatement stmtConsultatii = conn.prepareStatement(queryConsultatii);
             PreparedStatement stmtServicii = conn.prepareStatement(queryServicii);
             PreparedStatement stmtSalariu = conn.prepareStatement(insertOrUpdateSalariu)) {

            stmtAngajat.setInt(1, idAngajat);
            ResultSet rsAngajat = stmtAngajat.executeQuery();

            if (!rsAngajat.next()) {
                System.out.println("Angajatul nu a fost găsit.");
                return;
            }

            int salariuPeOra = rsAngajat.getInt("salariu_pe_ora");
            int nrOre = rsAngajat.getInt("nr_ore");
            String tipAngajat = rsAngajat.getString("tip_angajat");

            int salariuDinOre = salariuPeOra * nrOre;
            int bonusDinServicii = 0;

            if ("medical".equals(tipAngajat)) {
                stmtMedic.setInt(1, idAngajat);
                ResultSet rsMedic = stmtMedic.executeQuery();

                if (rsMedic.next()) {
                    int procentServicii = rsMedic.getInt("procent_servicii");
                    stmtConsultatii.setInt(1, idAngajat);
                    ResultSet rsConsultatii = stmtConsultatii.executeQuery();

                    while (rsConsultatii.next()) {
                        int idConsultatie = rsConsultatii.getInt("id_consultatie");
                        stmtServicii.setInt(1, idConsultatie);
                        ResultSet rsServicii = stmtServicii.executeQuery();

                        if (rsServicii.next()) {
                            int venitConsultatie = rsServicii.getInt(1);
                            bonusDinServicii += venitConsultatie * (100 + procentServicii) / 100;
                        }
                    }
                }
            }

            int salariuTotal = salariuDinOre + bonusDinServicii;

            stmtSalariu.setInt(1, idAngajat);
            stmtSalariu.setInt(2, luna);
            stmtSalariu.setInt(3, an);
            stmtSalariu.setInt(4, salariuTotal);
            stmtSalariu.setInt(5, bonusDinServicii);
            stmtSalariu.setInt(6, nrOre);
            stmtSalariu.setInt(7, salariuDinOre);

            stmtSalariu.executeUpdate();
            System.out.println("Salariul a fost actualizat cu succes pentru angajatul " + idAngajat);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Object[]> citesteCheltuieli() {
        List<Object[]> cheltuieli = new ArrayList<>();
        String query = "SELECT * FROM salariu";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Object[] row = new Object[]{
                        rs.getInt("id_salariu"),
                        rs.getInt("id_angajat"),
                        rs.getInt("luna"),
                        rs.getInt("an"),
                        rs.getInt("salariu_total"),
                        rs.getInt("bonus_din_servicii"),
                        rs.getInt("nr_ore_lucrate"),
                        rs.getInt("salariu_din_ore")
                };
                cheltuieli.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cheltuieli;
    }

    //=============================================================
    // ======== Modulul medical receptionist
    //=============================================================

    public static String getMedicalRoleByLoginID(String loginID) {
        String query = "SELECT m.tip_medical FROM medical m " +
                "JOIN angajat a ON m.id = a.id " +
                "JOIN utilizator u ON a.id = u.id " +
                "WHERE u.loginID = ? AND a.tip_angajat = 'medical'";

        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            stmt.setString(1, loginID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("tip_medical");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Returnează null dacă utilizatorul nu este medical sau nu există
    }

    public static ResultSet getServiciiDisponibile() {
        ResultSet resultSet = null;

        // Conectare la baza de date
        try {
            Connection conn = DatabaseConnection.getConnection();
            // Interogare SQL pentru a obține toate informațiile despre servicii
            String query = "SELECT id_serviciu, denumire, pret, durata FROM serviciu";

            // Pregătirea interogării
            Statement stmt = conn.createStatement();
            resultSet = stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultSet;
    }

    public static void creareProgramare(String idClient, String idMedic, String dataConsultatie, List<Integer> serviciiSelectate) {
        Connection conn = null;
        PreparedStatement stmtConsultatie = null;
        PreparedStatement stmtConsultatieServicii = null;
        ResultSet resultSet = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start tranzacție

            // Calculăm durata totală ca suma duratelor serviciilor selectate
            int durataTotala = 0;
            if (!serviciiSelectate.isEmpty()) {
                String placeholders = String.join(",", Collections.nCopies(serviciiSelectate.size(), "?"));
                String queryDurata = "SELECT SUM(durata) AS durata_totala FROM serviciu WHERE id_serviciu IN (" + placeholders + ")";
                PreparedStatement stmtDurata = conn.prepareStatement(queryDurata);

                for (int i = 0; i < serviciiSelectate.size(); i++) {
                    stmtDurata.setInt(i + 1, serviciiSelectate.get(i));
                }

                resultSet = stmtDurata.executeQuery();
                if (resultSet.next()) {
                    durataTotala = resultSet.getInt("durata_totala");
                }
                stmtDurata.close();
            }

            // Inserăm consultația în tabelul `consultatie`
            String queryConsultatie = "INSERT INTO consultatie (id_client, id_medic, data_consultatie, durata_totala) VALUES (?, ?, ?, ?)";
            stmtConsultatie = conn.prepareStatement(queryConsultatie, Statement.RETURN_GENERATED_KEYS);
            stmtConsultatie.setInt(1, Integer.parseInt(idClient));
            stmtConsultatie.setInt(2, Integer.parseInt(idMedic));
            stmtConsultatie.setDate(3, Date.valueOf(dataConsultatie));
            stmtConsultatie.setInt(4, durataTotala);
            stmtConsultatie.executeUpdate();

            // Obținem ID-ul consultației proaspăt inserate
            resultSet = stmtConsultatie.getGeneratedKeys();
            int idConsultatie = -1;
            if (resultSet.next()) {
                idConsultatie = resultSet.getInt(1);
            }

            // Inserăm serviciile asociate consultației
            String queryConsultatieServicii = "INSERT INTO consultatie_servicii (id_consultatie, id_serviciu) VALUES (?, ?)";
            stmtConsultatieServicii = conn.prepareStatement(queryConsultatieServicii);
            for (Integer idServiciu : serviciiSelectate) {
                stmtConsultatieServicii.setInt(1, idConsultatie);
                stmtConsultatieServicii.setInt(2, idServiciu);
                stmtConsultatieServicii.executeUpdate();
            }

            conn.commit(); // Finalizăm tranzacția
            System.out.println("Programare creată cu succes!");
            JOptionPane.showMessageDialog(new JOptionPane(), "Programare creată cu succes!");

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Revocăm modificările în caz de eroare
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            // Închidem resursele
            try {
                if (resultSet != null) resultSet.close();
                if (stmtConsultatie != null) stmtConsultatie.close();
                if (stmtConsultatieServicii != null) stmtConsultatieServicii.close();
                if (conn != null) conn.close();
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

    public static void adaugaPacient(String nume, String prenume, String cnp, String dataNasterii, String telefon) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            String query = "INSERT INTO client (nume, prenume, CNP, data_nasterii, nr_telefon) VALUES (?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(query);

            stmt.setString(1, nume);
            stmt.setString(2, prenume);
            stmt.setString(3, cnp);
            stmt.setDate(4, Date.valueOf(dataNasterii)); // Convertim String în java.sql.Date
            stmt.setString(5, telefon);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Pacient adăugat cu succes!");
            } else {
                System.out.println("Eroare la adăugarea pacientului.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static List<String[]> getTotiPacientii() {
        List<String[]> pacienti = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;

        try {
            conn = DatabaseConnection.getConnection();
            String query = "SELECT id_client, nume, prenume, CNP, data_nasterii, nr_telefon FROM client";
            stmt = conn.prepareStatement(query);
            resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                String[] pacient = new String[6];
                pacient[0] = String.valueOf(resultSet.getInt("id_client"));
                pacient[1] = resultSet.getString("nume");
                pacient[2] = resultSet.getString("prenume");
                pacient[3] = resultSet.getString("CNP");
                pacient[4] = String.valueOf(resultSet.getDate("data_nasterii"));
                pacient[5] = resultSet.getString("nr_telefon");

                pacienti.add(pacient);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return pacienti;
    }

    public static ResultSet getBonuri() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;

        try {
            // Stabilim conexiunea la baza de date
            conn = DatabaseConnection.getConnection();

            // Definim interogarea pentru a obține bonurile
            String query = "SELECT c.id_consultatie, cl.nume, cl.prenume, c.data_consultatie " +
                    "FROM consultatie c " +
                    "JOIN client cl ON c.id_client = cl.id_client";

            // Pregătim interogarea
            stmt = conn.prepareStatement(query);

            // Executăm interogarea
            resultSet = stmt.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Returnăm ResultSet-ul care va fi folosit pentru popularea tabelului
        return resultSet;
    }

    public static List<Map<String, String>> getDetailsForConsultatie(int idConsultatie) {
        List<Map<String, String>> listaConsultatii = new ArrayList<>();

        String query = "SELECT c.id_consultatie, cl.nume AS nume_client, cl.prenume AS prenume_client, " +
                "u.nume AS nume_medic, u.prenume AS prenume_medic, c.data_consultatie, c.durata_totala, " +
                "s.denumire AS serviciu, s.pret AS pret_serviciu, un.nume AS unitate " +
                "FROM consultatie c " +
                "JOIN client cl ON c.id_client = cl.id_client " +
                "JOIN utilizator u ON c.id_medic = u.id " +
                "JOIN consultatie_servicii cs ON c.id_consultatie = cs.id_consultatie " +
                "JOIN serviciu s ON cs.id_serviciu = s.id_serviciu " +
                "JOIN unitate_serviciu us ON s.id_serviciu = us.id_serviciu " +
                "JOIN unitate un ON us.id_unitate = un.id_unitate " +
                "WHERE c.id_consultatie = ? " +
                "GROUP BY c.id_consultatie, cl.nume, cl.prenume, u.nume, u.prenume, c.data_consultatie, c.durata_totala, s.denumire, s.pret, un.nume";


        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setInt(1, idConsultatie);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                Map<String, String> consultatie = new HashMap<>();
                consultatie.put("Nume Client", resultSet.getString("nume_client"));
                consultatie.put("Prenume Client", resultSet.getString("prenume_client"));
                consultatie.put("Nume Medic", resultSet.getString("nume_medic"));
                consultatie.put("Prenume Medic", resultSet.getString("prenume_medic"));
                consultatie.put("Data Consultației", resultSet.getDate("data_consultatie").toString());
                consultatie.put("Durata (minute)", String.valueOf(resultSet.getInt("durata_totala")));
                consultatie.put("Unitate", resultSet.getString("unitate"));
                consultatie.put("Serviciu", resultSet.getString("serviciu"));
                consultatie.put("Preț Serviciu", String.format(Locale.US, "%.2f", resultSet.getDouble("pret_serviciu")));


                listaConsultatii.add(consultatie);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listaConsultatii;
    }


    //=============================================================
    // ======== Modulul medical asistent/medic
    //=============================================================

    public static ResultSet getConsultatieDetails() {
        ResultSet resultSet = null;
        Connection conn = null;
        Statement stmt = null;

        try {
            // Obține conexiunea la baza de date
            conn = DatabaseConnection.getConnection();

            // Creăm interogarea SQL, acum includem și id_consultatie
            String query = "SELECT cons.id_consultatie, c.nume AS client_nume, c.prenume AS client_prenume, " +
                    "u.nume AS medic_nume, u.prenume AS medic_prenume, " +
                    "cons.id_client, cons.id_medic, cons.data_consultatie, " +
                    "GROUP_CONCAT(s.denumire) AS servicii " +
                    "FROM consultatie cons " +
                    "JOIN client c ON cons.id_client = c.id_client " +
                    "JOIN utilizator u ON cons.id_medic = u.id " +
                    "JOIN consultatie_servicii cs ON cons.id_consultatie = cs.id_consultatie " +
                    "JOIN serviciu s ON cs.id_serviciu = s.id_serviciu " +
                    "GROUP BY cons.id_consultatie";

            // Creăm un statement și executăm interogarea
            stmt = conn.createStatement();
            resultSet = stmt.executeQuery(query);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultSet;
    }

    public static void salvareRaport(String consultatieID, String pacientID, String medicID, String numeAnaliza,
                                     String rezultatAnaliza, String medicRecomandator, String asistent, String istoric,
                                     String simptome, String investigatii, String diagnostic, String ecomandati) {
        // Obținem conexiunea la baza de date
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Definim SQL-ul pentru inserarea datelor în tabelul raport
            String sql = "INSERT INTO raport (id_consultatie, id_pacient, id_medic, nume_analiza, rezultat_analiza, " +
                    "id_medic_recomandator, id_asistent, istoric, simptome, investigatii, diagnostic, recomandari) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            // Pregătim instrucțiunea SQL
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                // Setăm valorile parametrilor din interogare
                stmt.setInt(1, Integer.parseInt(consultatieID));  // id_consultatie
                stmt.setInt(2, Integer.parseInt(pacientID));      // id_pacient
                stmt.setInt(3, Integer.parseInt(medicID));        // id_medic
                stmt.setString(4, numeAnaliza);                   // nume_analiza
                stmt.setString(5, rezultatAnaliza);               // rezultat_analiza

                // Verificăm dacă medicRecomandator este gol
                if (medicRecomandator.isEmpty()) {
                    stmt.setNull(6, java.sql.Types.INTEGER);      // id_medic_recomandator (setăm NULL dacă e gol)
                } else {
                    stmt.setInt(6, Integer.parseInt(medicRecomandator)); // id_medic_recomandator
                }

                if (asistent.isEmpty()) {
                    stmt.setNull(7, java.sql.Types.INTEGER);
                } else {
                    stmt.setInt(7, Integer.parseInt(asistent));
                }

                stmt.setString(8, istoric);                       // istoric
                stmt.setString(9, simptome);                      // simptome
                stmt.setString(10, investigatii);                 // investigatii
                stmt.setString(11, diagnostic);                   // diagnostic
                stmt.setString(12, ecomandati);                   // recomandari

                // Executăm inserarea în baza de date
                stmt.executeUpdate();
                System.out.println("Raportul a fost salvat cu succes!");
            } catch (SQLException e) {
                System.out.println("Eroare la pregătirea sau executarea interogării: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Eroare la conexiunea la baza de date: " + e.getMessage());
        }
    }

    public static ResultSet getRaportData() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();

        // Interogare pentru a obține informațiile complete din tabel
        String query = "SELECT r.id_consultatie, r.nume_analiza, r.rezultat_analiza, r.id_medic_recomandator, " +
                "r.id_asistent, r.istoric, r.simptome, r.investigatii, r.diagnostic, r.recomandari, " +
                "c.nume AS nume_pacient, c.prenume AS prenume_pacient, m.nume AS nume_medic, m.prenume AS prenume_medic, " +
                "GROUP_CONCAT(s.denumire SEPARATOR ', ') AS servicii_oferite " +
                "FROM raport r " +
                "JOIN consultatie cons ON r.id_consultatie = cons.id_consultatie " +
                "JOIN client c ON cons.id_client = c.id_client " +
                "JOIN utilizator m ON cons.id_medic = m.id " +
                "LEFT JOIN consultatie_servicii cs ON cons.id_consultatie = cs.id_consultatie " +
                "LEFT JOIN serviciu s ON cs.id_serviciu = s.id_serviciu " +
                "GROUP BY r.id_raport";

        Statement stmt = conn.createStatement();
        return stmt.executeQuery(query); // Returnează ResultSet-ul cu datele
    }



}
