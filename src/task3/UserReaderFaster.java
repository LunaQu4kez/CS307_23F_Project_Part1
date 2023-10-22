package task3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class UserReaderFaster {
    private static final int  BATCH_SIZE = 500;
    private Connection con = null;
    private PreparedStatement stmt = null;
    private ResultSet resultSet;

    private String host = "localhost";
    private String dbname = "CS307_proj1";
    private String user = "postgres";
    private String pwd = "12211655";
    private String port = "5432";
    public static void main(String[] args) {
        String filePath = "data\\users.csv";
        UserReaderFaster userReader = new UserReaderFaster();
        userReader.insertUser(filePath);
//        userReader.insertFollowing(filePath);
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

    public void truncateFollowing() {
        String sql = "truncate table project_following cascade";
        try {
            stmt = con.prepareStatement(sql);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void truncateUser() {
        String sql = "truncate table project_user cascade";
        try {
            stmt = con.prepareStatement(sql);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertUser(String filePath) {
        openDB();
        truncateUser();
        closeDB();


        long start,end;
        start =System.currentTimeMillis();
        openDB();
        try {
            BufferedReader in = new BufferedReader(new FileReader(filePath));
            in.readLine();
            String line = in.readLine();
            String[][] result;
            long count = 0;
            stmt = con.prepareStatement("insert into project_user values (?,?,?,?,?,?,?)");
            while ((line != null) && count <=500) {
                try {
                    if (!line.endsWith("user")) {
                        line = line + "\n" + in.readLine();
                        continue;
                    }
                    result = processUser(line);
                    loadDataOfUser(result);
                    count++;
                    if (count% BATCH_SIZE == 0){
                        stmt.executeBatch();
                        stmt.clearBatch();
                    }
                    line = in.readLine();
                } catch (Exception e) {
                    System.out.println("Insertion failure.");
                    System.out.println(line);
                    line = in.readLine();
                }
            }
            if (count % BATCH_SIZE !=0){
                stmt.executeBatch();
            }
            con.commit();
            closeDB();
        } catch (SQLException se) {
            System.err.println("SQL error: " + se.getMessage());
            try {
                con.rollback();
                stmt.close();
            } catch (Exception e2) {
            }
            closeDB();
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Fatal error: " + e.getMessage());
            try {
                con.rollback();
                stmt.close();
            } catch (Exception e2) {
            }
            closeDB();
            System.exit(1);
        }
        closeDB();
        end = System.currentTimeMillis();
        System.out.println("Total time: "+ (end-start) +" ms");
    }

    private void insertFollowing(String filePath) {
        openDB();
        truncateFollowing();
        closeDB();

        openDB();
        try {
            BufferedReader in = new BufferedReader(new FileReader(filePath));
            in.readLine();
            String line = in.readLine();
            String[][] result;
            long count = 0;
            while ((line != null)) {
                try {
                    if (!line.endsWith("user")) {
                        line = line + "\n" + in.readLine();
                        continue;
                    }
                    result = processUser(line);
                    stmt = con.prepareStatement("insert into project_following values (?,?)");
                    loadDataOfFollowing(result);
                    line = in.readLine();
                } catch (Exception e) {
                    System.out.println("Insertion failure.");
                    System.out.println(line);
                    line = in.readLine();
                }
            }
            closeDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadDataOfUser(String[][] result) {
        try {
            if (con != null) {
                stmt.setLong(1, Long.parseLong(result[0][0]));
                stmt.setString(2, result[1][0]);
                stmt.setString(3, result[2][0]);
                if (result[3][0].equals("")) {
                    stmt.setString(4, null);
                } else {
                    stmt.setString(4, result[3][0]);
                }
                stmt.setInt(5, Integer.parseInt(result[4][0]));
                if (result[5][0].equals("")) {
                    stmt.setString(6, null);
                } else {
                    stmt.setString(6, result[5][0]);
                }
                stmt.setString(7, result[7][0]);
                stmt.addBatch();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadDataOfFollowing(String[][] result) {
        try {
            if (con != null) {
                if (result[6] == null) {
                    return;
                }
                stmt.setLong(1, Long.parseLong(result[0][0]));
                for (int i = 0; i < result[6].length; i++) {
                    stmt.setLong(2, Long.parseLong(result[6][i]));
                    stmt.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static String[][] processUser(String info) {
        String[][] result = new String[8][];
        for (int i = 0; i < 8; i++) {
            if (i == 6) {
                continue;
            }
            result[i] = new String[1];
        }
        String[] firstArr = info.split("\\[|\\]");          //[或]分隔
        if (firstArr.length > 3) {
            String reArr[] = info.split(",");
            if (reArr[0].equals("268135")) {
                reArr[5] = reArr[5] + "," + reArr[6];
            }
            for (int i = 0; i < 6; i++) {
                result[i][0] = reArr[i];
            }
        } else {
            if (firstArr[0].charAt(firstArr.length - 1) != '"') {
                firstArr[0] = firstArr[0] + "\"";                     //如果没有following，手动添加“方便处理
            }
            String[] secArr = firstArr[0].split(",");           //逗号分隔
            int sexIndex = 2;
            if (!(secArr[2].equals("男") || secArr[2].equals("女") || secArr[2].equals("保密"))) {
                while (!secArr[sexIndex].equals("男")
                        && !secArr[sexIndex].equals("女")
                        && !secArr[sexIndex].equals("保密")) {
                    secArr[1] = secArr[1] + "," + secArr[sexIndex];
                    sexIndex++;
                }
            }
            if (!secArr[sexIndex + 4].equals("\"")) {                                  //拼接逗号分割开的签名
                for (int i = sexIndex + 4; i < secArr.length - 1; i++) {
                    secArr[sexIndex + 3] = secArr[sexIndex + 3] + "," + secArr[i];
                }
            }

            //前六项加入结果数组
            for (int i = 0; i < 2; i++) {
                //System.out.println(i);
                result[i][0] = secArr[i];
            }
            for (int i = sexIndex; i < sexIndex + 4; i++) {
                result[i - sexIndex + 2][0] = secArr[i];
            }
        }

        String[] following = firstArr[firstArr.length - 2].split(",");
        if (following.length == 1 && following[0].equals("")) {
            result[6] = null;
        } else {
            following[0] = following[0].substring(1, following[0].length() - 1);
            for (int i = 1; i < following.length; i++) {
                following[i] = following[i].substring(2, following[i].length() - 1);
            }
            result[6] = following;
        }

        if (firstArr[firstArr.length -1 ].charAt(firstArr[firstArr.length -1].length() - 5) == ',') {
            result[7][0] = "user";
        } else {
            result[7][0] = "superuser";
        }
        if (result[3][0].endsWith("日")){
            String[] arr1 = result[3][0].split("月");
            String str = arr1[0].length() == 1 ? "0"+arr1[0].substring(0,1) : arr1[0].substring(0,2);
            str = arr1[1].length() == 2 ? str +"-"+ "0"+arr1[1].substring(0,1) : str +"-"+ arr1[1].substring(0,2);
            result[3][0] = str;
        }
        return result;
    }
}