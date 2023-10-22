package task3;

import javax.print.DocFlavor;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.*;
import java.util.Arrays;
import java.util.IllegalFormatCodePointException;
import java.util.regex.Pattern;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;


public class VideosReader {

    private Connection con = null;
    private PreparedStatement stmt = null;
    private ResultSet resultSet;

    private String host = "localhost";
    private String dbname = "CS307_proj1";
    private String user = "postgres";
    private String pwd = "12211655";
    private String port = "5432";

    public static void main(String[] args) {
        String filePath = "data\\videos.csv";
        VideosReader reader = new VideosReader();
        reader.insertVideos(filePath);
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

    public void truncateVideos() {
        String sql = "truncate table project_videos cascade";
        try {
            stmt = con.prepareStatement(sql);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertVideos(String filePath) {
        openDB();
        truncateVideos();
        closeDB();
        long start, end;
        start = System.currentTimeMillis();
        openDB();
        int count = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(filePath));
            FileWriter out = new FileWriter("data\\video3.txt");
            in.readLine();
            String line = in.readLine();
            String[][] result;

            while ((line != null) ) {
                try {
                    if (!line.endsWith(")]\"")) {
                        line = line + "\n" + in.readLine();
                        continue;
                    }
                    result = processVideo(line);
                    stmt = con.prepareStatement("insert into project_videos values (?,?,?,?,?,?,?,?,?)");
                    loadDataOfVideos(result);
                    line = in.readLine();

                } catch (Exception e) {
                    System.out.println("Insertion failure.");
                    count++;
                    out.write(line);
                    out.write("\n");
                    line = in.readLine();
                }

            }
            closeDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
        end = System.currentTimeMillis();
        System.out.println("Total time: " + (end - start) + "ms");
        System.out.println(count);

    }

    private void loadDataOfVideos(String[][] result) throws Exception {

            if (con != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                stmt.setString(1,result[0][0]);
                stmt.setString(2,result[1][0]);
                stmt.setLong(3,Long.parseLong(result[2][0]));
                Date parsedDate = dateFormat.parse(result[3][0]);
                stmt.setTimestamp(4,new java.sql.Timestamp(parsedDate.getTime()));
                parsedDate = dateFormat.parse(result[4][0]);
                stmt.setTimestamp(5,new java.sql.Timestamp(parsedDate.getTime()));
                parsedDate = dateFormat.parse(result[5][0]);
                stmt.setTimestamp(6,new java.sql.Timestamp(parsedDate.getTime()));
                stmt.setInt(7,Integer.parseInt(result[6][0]));
                if (result[7][0].equals("")){
                    stmt.setString(8,null);
                }else {
                    stmt.setString(8,result[7][0]);
                }
                stmt.setLong(9,Long.parseLong(result[8][0]));
                stmt.executeUpdate();
            }



    }

    private static String[][] processVideo(String info) {
        String[][] result = new String[13][];
        for (int i = 0; i < 9; i++) {
            result[i] = new String[1];
        }
        String[] firstArr = info.split("\\[|\\]");

        String[] like = firstArr[firstArr.length - 8].split(",");
        like[0] = like[0].substring(1, like[0].length() - 1);
        for (int i = 1; i < like.length; i++) {
            like[i] = like[i].substring(2, like[i].length() - 1);
        }
        result[9] = like;

        String[] coin = firstArr[firstArr.length - 6].split(",");
        coin[0] = coin[0].substring(1, coin[0].length() - 1);
        for (int i = 1; i < coin.length; i++) {
            coin[i] = coin[i].substring(2, coin[i].length() - 1);
        }
        result[10] = coin;

        String[] fav = firstArr[firstArr.length - 4].split(",");
        fav[0] = fav[0].substring(1, fav[0].length() - 1);
        for (int i = 1; i < fav.length; i++) {
            fav[i] = fav[i].substring(2, fav[i].length() - 1);
        }
        result[11] = fav;

        String[] view1 = firstArr[firstArr.length - 2].split("\\(|\\)");
        String[] view = new String[view1.length / 2];
        for (int i = 0; i < view.length; i++) {
            view[i] = view1[2 * i + 1];
        }
        result[12] = view;

        String[] secondArr = info.split(",");
        int index = 2;
        result[0][0] = secondArr[0];

        while (!isNumeric(secondArr[index])) {
            secondArr[1] = secondArr[1] + "," + secondArr[index];
            index++;
        }
        result[1][0] = secondArr[1];
        result[2][0] = secondArr[index];
        String pattern = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";
        while (!Pattern.matches(pattern, secondArr[index])) {
            index++;
        }

        String[] thirdArr = info.split("\"");
        String[] forthArr = thirdArr[thirdArr.length-8].split(",");
        result[8][0] = forthArr[forthArr.length-1];

        result[3][0] = secondArr[index];
        result[4][0] = secondArr[index + 1];
        result[5][0] = secondArr[index + 2];
        result[6][0] = secondArr[index + 3];
        index += 4;
        result[7][0] = secondArr[index];
        index++;
        while (!secondArr[index].equals(result[8][0])) {
            result[7][0] = result[7][0] + "," + secondArr[index];
            index++;
        }
        result[8][0] = secondArr[index];
        return result;
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

//    private static String[] returnView(){
//
//
//    }
}
