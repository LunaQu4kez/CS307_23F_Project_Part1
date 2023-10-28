package task3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DanmuReader {
    private Connection conn = null;
    private PreparedStatement stmt = null;
    private static String filepath = "data\\danmu.csv";
    private static int pk_id = 1;

    public void openDB() {
        String host = "localhost";
        String port = "5432";
        String db_name = "project1";
        String user = "postgres";
        String pw = "123456";

        String url = "jdbc:postgresql://" + host + ":" + port + "/" + db_name;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url, user, pw);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeDB() {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void truncateDanmu() {
        String sql = "truncate table project_danmu cascade";
        try {
            stmt = conn.prepareStatement(sql);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadDanmu(String str) {
        String[] data = str.split(",");
        String bv = data[0];
        long mid = Long.parseLong(data[1]);
        float time = Float.parseFloat(data[2]);
        StringBuilder content = new StringBuilder();
        if (data.length >= 4) content.append(data[3]);
        for (int i = 4; i < data.length; i++) {
            content.append(data[i]);
        }
        String sql = "insert into project_danmu values (?, ?, ?, ?, ?)";
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, pk_id++);
            stmt.setString(2, bv);
            stmt.setLong(3, mid);
            stmt.setFloat(4, time);
            stmt.setString(5, content.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertDanmu() {
        openDB();
        truncateDanmu();
        closeDB();

        long start, end;
        start = System.currentTimeMillis();
        openDB();
        try {
            BufferedReader bf = new BufferedReader(new FileReader(filepath));
            bf.readLine();
            String line = bf.readLine();
            StringBuilder sb = new StringBuilder().append(line);
            while ((line = bf.readLine()) != null) {
                if (line.startsWith("BV")) {
                    String str = sb.toString();
                    sb = new StringBuilder().append(line);
                    loadDanmu(str);
                } else {
                    sb.append("\n").append(line);
                }
            }
            loadDanmu(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            closeDB();
        }
        closeDB();
        end = System.currentTimeMillis();
        System.out.println((end - start) + "ms");
    }

    public static void main(String[] args) {
        DanmuReader dr = new DanmuReader();
        dr.insertDanmu();
    }
}
