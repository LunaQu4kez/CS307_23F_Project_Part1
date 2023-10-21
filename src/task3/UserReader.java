package task3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserReader {
    private Connection con = null;
    private ResultSet resultSet;

    private String host = "192.168.1.3";
    private String dbname = "CS307_proj1";
    private String user = "postgres";
    private String pwd = "12211655";
    private String port = "5432";

    private void openDB(){
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbname;
        try {
            con = DriverManager.getConnection(url,user,pwd);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
