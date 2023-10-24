package task3;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VideosReaderMoreFaster {
    private static String filepath = "data\\videos.csv";
    private static String driver = "org.postgresql.Driver";
    private static String ip = "localhost";
    private static String port = "5432";
    private static String db_name = "project1";
    private static String user = "postgres";
    private static String pw = "123456";
    private static String url = "jdbc:postgresql://" + ip + ":" + port + "/" + db_name;;

    public void getDriver() {
        try {
            Class.forName(driver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void truncateTable(String table) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url, user, pw);
            PreparedStatement stmt = conn.prepareStatement("truncate table " + table + " cascade");
            stmt.execute();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        VideosReaderMoreFaster v = new VideosReaderMoreFaster();
        v.getDriver();
        v.insertLike();
        v.insertCoin();
        v.insertFav();
        v.insertView();
    }

    private void insertLike() {
        insert("project_like", 10);
    }

    private void insertCoin() {
        insert("project_coin", 11);
    }

    private void insertFav() {
        insert("project_favorite", 12);
    }

    private void insertView() {
        truncateTable("project_view");
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        try {
            CSVReader sc = new CSVReader(new FileReader(filepath));
            sc.readNext();
            String[] data;
            int dealData = 0;
            int ONE_THREAD_DEAL = 85;
            List<String> bvs = new ArrayList<>();
            List<long[]> mids = new ArrayList<>();
            List<int[]> times = new ArrayList<>();
            while ((data = sc.readNext()) != null) {
                String[] pairs = data[13].substring(2, data[13].length() - 2).split("\\), \\(");
                long[] mid = new long[pairs.length];
                int[] time = new int[pairs.length];
                for (int i = 0; i < pairs.length; i++) {
                    String[] info = pairs[i].split(", ");
                    mid[i] = Long.parseLong(info[0].substring(1, info[0].length() - 1));
                    time[i] = Integer.parseInt(info[1]);
                }
                bvs.add(data[0]);
                mids.add(mid);
                times.add(time);
                dealData++;
                if (dealData % ONE_THREAD_DEAL == 0) {
                    BThread b = new BThread(bvs, mids, times);
                    executorService.execute(b);
                    bvs = new ArrayList<>();
                    mids = new ArrayList<>();
                    times = new ArrayList<>();
                }
            }
            BThread b = new BThread(bvs, mids, times);
            executorService.execute(b);
            executorService.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
            executorService.shutdown();
        }
    }

    private void insert(String table, int idx) {
        truncateTable(table);
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        try {
            CSVReader sc = new CSVReader(new FileReader(filepath));
            sc.readNext();
            String[] data;
            int dealData = 0;
            int ONE_THREAD_DEAL = 85;
            int threads = 0;
            List<String> bvs = new ArrayList<>();
            List<long[]> mids = new ArrayList<>();
            while ((data = sc.readNext()) != null) {
                String[] users = data[idx].substring(1, data[idx].length() - 1).split(", ");
                long[] mid = new long[users.length];
                for (int i = 0; i < mid.length; i++)
                    mid[i] = Long.parseLong(users[i].substring(1, users[i].length() - 1));
                bvs.add(data[0]);
                mids.add(mid);
                dealData++;
                if (dealData % ONE_THREAD_DEAL == 0) {
                    AThread a = new AThread(table, bvs, mids);
                    executorService.execute(a);
                    System.out.println(++threads);
                    bvs = new ArrayList<>();
                    mids = new ArrayList<>();
                }
            }
            AThread a = new AThread(table, bvs, mids);
            executorService.execute(a);
            executorService.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
            executorService.shutdown();
        }
    }

    private class AThread extends Thread {
        String table_name;
        List<String> bvs;
        List<long[]> mids;

        public AThread(String table_name, List<String> bvs, List<long[]> mids) {
            this.table_name = table_name;
            this.bvs = bvs;
            this.mids = mids;
        }

        @Override
        public void run() {
            Connection conn = null;
            PreparedStatement stmt = null;
            try {
                conn = DriverManager.getConnection(url, user, pw);
                conn.setAutoCommit(false);
                String sql = "insert into " + table_name + " values (?, ?)";
                stmt = conn.prepareStatement(sql);
                for (int k = 0; k < bvs.size(); k++) {
                    String bv = bvs.get(k);
                    long[] mid = mids.get(k);
                    stmt.setString(1, bv);
                    for (int i = 0; i < mid.length; i += 1000) {
                        for (int j = 0; j < Math.min(1000, mid.length - i); j++) {
                            stmt.setLong(2, mid[i + j]);
                            stmt.addBatch();
                        }
                        stmt.executeBatch();
                        stmt.clearBatch();
                    }
                }
                conn.commit();
                stmt.close();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    if (stmt != null)
                        stmt.close();
                    if (conn != null)
                        conn.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private class BThread extends Thread {
        List<String> bvs;
        List<long[]> mids;
        List<int[]> times;

        public BThread(List<String> bvs, List<long[]> mids, List<int[]> times) {
            this.bvs = bvs;
            this.mids = mids;
            this.times = times;
        }

        @Override
        public void run() {
            Connection conn = null;
            PreparedStatement stmt = null;
            try {
                conn = DriverManager.getConnection(url, user, pw);
                conn.setAutoCommit(false);
                String sql = "insert into project_view values (?, ?, ?)";
                stmt = conn.prepareStatement(sql);
                for (int k = 0; k < bvs.size(); k++) {
                    String bv = bvs.get(k);
                    long[] mid = mids.get(k);
                    int[] time = times.get(k);
                    stmt.setString(1, bv);
                    for (int i = 0; i < mid.length; i += 1000) {
                        for (int j = 0; j < Math.min(1000, mid.length - i); j++) {
                            stmt.setLong(2, mid[i + j]);
                            stmt.setInt(3, time[i + j]);
                            stmt.addBatch();
                        }
                        stmt.executeBatch();
                        stmt.clearBatch();
                    }
                }
                conn.commit();
                stmt.close();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    if (stmt != null)
                        stmt.close();
                    if (conn != null)
                        conn.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
