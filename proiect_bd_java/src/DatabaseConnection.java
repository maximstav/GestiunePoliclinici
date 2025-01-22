/*
Proiect la disciplina: Baza de Date
Titlu: Sistem Informatizat de Gestiune a unui Lanț de Policlinici
Realizat de: Staver Maxim, grupa 30223
*/
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/proiect_policlinica_max";
    private static final String USER = "root";
    private static final String PASSWORD = "password";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}