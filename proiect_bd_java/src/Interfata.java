/*
Proiect la disciplina: Baza de Date
Titlu: Sistem Informatizat de Gestiune a unui Lanț de Policlinici
Realizat de: Staver Maxim, grupa 30223
*/
//Acest fisier contine interfata de logare

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Interfata extends JFrame {
    private JTextField txtLoginID;
    private JPasswordField txtParola;
    private JLabel lblMessage;

    public Interfata() {
        setTitle("Autentificare");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblLogin = new JLabel("Login ID:");
        lblLogin.setBounds(50, 50, 100, 25);
        add(lblLogin);

        txtLoginID = new JTextField();
        txtLoginID.setBounds(150, 50, 150, 25);
        add(txtLoginID);

        JLabel lblParola = new JLabel("Parola:");
        lblParola.setBounds(50, 100, 100, 25);
        add(lblParola);

        txtParola = new JPasswordField();
        txtParola.setBounds(150, 100, 150, 25);
        add(txtParola);

        lblMessage = new JLabel("", SwingConstants.CENTER);
        lblMessage.setBounds(50, 130, 250, 25);
        add(lblMessage);

        JButton btnLogin = new JButton("Autentificare");
        btnLogin.setBounds(100, 170, 150, 30);
        add(btnLogin);

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autentificare();
            }
        });

        setVisible(true);
    }

    private void autentificare() {
        String loginID = txtLoginID.getText();
        String parola = new String(txtParola.getPassword());

        if (loginID.isEmpty() || parola.isEmpty()) {
            lblMessage.setText("Completați toate câmpurile!");
            return;
        }

        String tipUtilizator = UtilizatorDAO.verificaAutentificare(loginID, parola);
        if (tipUtilizator != null) {
            JOptionPane.showMessageDialog(this, "Autentificare reușită! Tip utilizator: " + tipUtilizator);
            deschideFereastraUtilizator(tipUtilizator);
        } else {
            lblMessage.setText("LoginID sau parolă incorectă!");
        }
    }


    private void deschideFereastraUtilizator(String tipUtilizator) {
        this.setVisible(false); // Ascundem fereastra de login

        switch (tipUtilizator) {
            case "Administrator":
                new FereastraAdministrator();
                break;
            case "Super-Administrator":
                new FereastraSuperAdministrator();
                break;
            case "Angajat":
                new FereastraAngajat(txtLoginID.getText());
                break;
            default:
                JOptionPane.showMessageDialog(this, "Tip utilizator necunoscut!");
                this.setVisible(true); // Dacă e necunoscut, reafișăm fereastra de login
        }
    }


}
