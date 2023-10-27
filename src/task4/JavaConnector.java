package task4;

import com.opencsv.CSVReader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JavaConnector {

    private Connection con = null;
    private PreparedStatement stmt = null;

    private String host = "localhost";
    private String dbname = "CS307_proj1";
    private String user = "postgres";
    private String pwd = "12211655";
    private String port = "5432";
    private int openAndeCloseCaseNum = 10;
    //It means testing openDB and cloeDB 10 times.

    public static void main(String[] args) {
        JavaConnector connector = new JavaConnector();

        long start , end;
        start = System.currentTimeMillis();
        for (int i = 0; i < connector.openAndeCloseCaseNum; i++) {
            connector.openDB();
            connector.closeDB();
        }
        end = System.currentTimeMillis();
        System.out.println("Open and close db "+ connector.openAndeCloseCaseNum +" times use "+
                (end -start)+" ms");
    }

    private void openDB() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Cannot find the Postgres driver. Check CLASSPATH.");
            System.exit(1);
        }
        String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbname;
        try {
            con = DriverManager.getConnection(url, user, pwd);
        } catch (SQLException e) {
            System.err.println("Database connection failed");
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private void closeDB() {
        if (con != null) {
            try {
                con.close();
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
