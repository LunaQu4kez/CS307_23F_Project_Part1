package task4;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVParserBuilder;

import com.opencsv.exceptions.CsvException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class JavaConnector {

    private Connection con = null;
    private PreparedStatement stmt = null;

    private String host = "localhost";
    private String dbname = "project1";
    private String user = "postgres";
    private String pwd = "123456";
    private String port = "5432";
    private int openAndeCloseCaseNum = 10;
    private int BATCH_SIZE = 1000;
    private ResultSet resultSet = null;

    private static String filePath = "data\\users.csv";
    //It means testing openDB and cloeDB 10 times.

    public static void main(String[] args) {
        JavaConnector connector = new JavaConnector();
        //testConnect(connector);
        //connector.insertUser(filePath);
        testQuery(connector);

    }

    private static void testConnect(JavaConnector connector){
        long start, end;
        start = System.currentTimeMillis();
        for (int i = 0; i < connector.openAndeCloseCaseNum; i++) {
            connector.openDB();
            connector.closeDB();
        }
        end = System.currentTimeMillis();
        System.out.println("Open and close db " + connector.openAndeCloseCaseNum + " times use " +
                (end - start) + " ms");
    }

    private static ResultSet testQuery(JavaConnector connector){
//        ArrayList <Long> list = new ArrayList<>();
        long start, end;
        String sql = "select mid from project_user";
        connector.openDB();
        try {
            connector.con.setAutoCommit(true);
            start = System.currentTimeMillis();
            connector.stmt = connector.con.prepareStatement(sql);
            connector.resultSet = connector.stmt.executeQuery();
            end = System.currentTimeMillis();
            System.out.println("Query mid for all users: "+(end -start) + " ms");
//            while (connector.resultSet.next()){
//                list.add(connector.resultSet.getLong("mid"));
//            }
//            System.out.println(list.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sql = "select mid from project_user where level > 3";
        try {
            start = System.currentTimeMillis();
            connector.stmt = connector.con.prepareStatement(sql);
            connector.resultSet = connector.stmt.executeQuery();
            end = System.currentTimeMillis();
            System.out.println("Query the mid of all users whose level is greater than 3: "+(end -start) + " ms");
//           while (connector.resultSet.next()){
//               list.add(connector.resultSet.getLong("mid"));
//            }
//            System.out.println(list.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        sql = "select mid from project_user where cast(mid as varchar) like '24%'";
        try {
            start = System.currentTimeMillis();
            connector.stmt = connector.con.prepareStatement(sql);
            connector.resultSet = connector.stmt.executeQuery();
            end = System.currentTimeMillis();
            System.out.println("Query the mid of all users whose mid starts with 24: "+(end -start) + " ms");
//           while (connector.resultSet.next()){
//               list.add(connector.resultSet.getLong("mid"));
//            }
//            System.out.println(list.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        connector.closeDB();

        return connector.resultSet;
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
            con.setAutoCommit(false);
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

    public void truncateUser() {
        String sql = "truncate table project_user cascade";
        try {
            stmt = con.prepareStatement(sql);
            stmt.execute();
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertUser(String filePath) {
        openDB();
        truncateUser();
        closeDB();

        long start, end;
        start = System.currentTimeMillis();
        openDB();
        try {
            CSVParserBuilder csvParserBuilder = new CSVParserBuilder();
            char c = (char) 1;
            csvParserBuilder.withEscapeChar(c);
            CSVReaderBuilder csvReaderBuilder = new CSVReaderBuilder(new FileReader(filePath));
            csvReaderBuilder.withCSVParser(csvParserBuilder.build());
            CSVReader in = csvReaderBuilder.build();
//            CSVReader in = new CSVReader(new FileReader(filePath));
            long count = 0;
            in.readNext();
            String[] result = in.readNext();
            stmt = con.prepareStatement("insert into project_user values (?,?,?,?,?,?,?)");
            while ((result != null)) {
                if (result.length != 8){
                    System.out.println(result.length);
                    System.out.println(Arrays.toString(result));
                }
                try {
                    loadDataOfUser(result);
                    count++;
                    if (count % BATCH_SIZE == 0) {
                        stmt.executeBatch();
                        stmt.clearBatch();
                    }
                    result = in.readNext();
                } catch (Exception e) {

                    System.out.println("Insertion failure.");
                    System.out.println("In: " + count);
                    result = in.readNext();
                }
            }
            if (count % BATCH_SIZE != 0) {
                stmt.executeBatch();
                stmt.clearBatch();
            }
            con.commit();
            closeDB();
        } catch (Exception e) {
            System.out.println("Insertion failure.");
            e.printStackTrace();
        }
        closeDB();
        end = System.currentTimeMillis();
        System.out.println("Insert use " + (end - start) + " ms");
    }

    private void loadDataOfUser(String[] result) {
        try {
            if (con != null) {
                stmt.setLong(1, Long.parseLong(result[0]));
                stmt.setString(2, result[1]);
                stmt.setString(3, result[2]);
                if (result[3].equals("")) {
                    stmt.setString(4, null);
                } else {
                    stmt.setString(4, result[3]);
                }
                stmt.setInt(5, Integer.parseInt(result[4]));
                if (result[5].equals("")) {
                    stmt.setString(6, null);
                } else {
                    stmt.setString(6, result[5]);
                }
                stmt.setString(7, result[7]);
                stmt.addBatch();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
