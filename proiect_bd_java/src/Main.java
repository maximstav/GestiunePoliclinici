/*
 Proiect la disciplina: Baza de Date
 Titlu: Sistem Informatizat de Gestiune a unui Lanț de Policlinici

 Realizat de: Staver Maxim, grupa 30223

 Data prezentării: 14 ianuarie 2025
 Profesor curs: Cosmina Ivan
 Îndrumător laborator: Teodora Tat

 Descriere:
 Acest sistem informatizat permite gestionarea utilizatorilor (administratori, super-administratori și angajați),
 a programărilor pacienților, a concediilor și salariilor angajaților, precum și a rapoartelor medicale și financiare.
 Aplicația oferă o interfață grafică pentru interacțiunea cu baza de date MySQL, fiind dezvoltată în Java
 utilizând JDBC si Swing.
 */


import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Interfata());
    }
}