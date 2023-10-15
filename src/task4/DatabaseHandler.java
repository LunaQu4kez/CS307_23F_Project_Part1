package task4;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class DatabaseHandler implements DataHandler {
    private Connection conn = null;
    private PreparedStatement stmt = null;
    private ResultSet rs = null;

    private static final int threadNum = 50;
    private static final int dataTotalNum = 100000;
    private static final int dataNum = dataTotalNum / threadNum;
    private static long cnt = 10000000000000000L;

    public void openDB() {
        ResourceBundle bundle = ResourceBundle.getBundle("jdbc");
        String driver = bundle.getString("driver");
        String ip = bundle.getString("ip");
        String port = bundle.getString("port");
        String db_name = bundle.getString("db_name");
        String user = bundle.getString("user");
        String pw = bundle.getString("password");

        String url = "jdbc:postgresql://" + ip + ":" + port + "/" + db_name;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, pw);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeDB() {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
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

    public void truncateUser() {
        String sql = "truncate table project_user cascade";
        try {
            stmt = conn.prepareStatement(sql);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> queryAllUsersName() {
        List<String> list = new ArrayList<>();
        String sql = "select name from project_user";
        try {
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                list.add(name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<String> queryNameByMIDRange(long min, long max) {
        List<String> list = new ArrayList<>();
        String sql = "select name from project_user where ? <= mid & mid <= ?";
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, min);
            stmt.setLong(2, max);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                list.add(name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<String> queryNameByLevel(int level) {
        List<String> list = new ArrayList<>();
        String sql = "select name from project_user where level = ?";
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, level);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                list.add(name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<String> queryNameByMID2Digits(char c1, char c2) {
        List<String> list = new ArrayList<>();
        String sql = "select name from project_user where mid = ?";
        try {
            stmt = conn.prepareStatement(sql);
            String module = "'" + c1 + "" + c2 + "%'";
            stmt.setString(1, module);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                list.add(name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<String> queryLongestName() {
        List<String> list = new ArrayList<>();
        String sql = "select name from project_user where length(name) = (select max(length(name)) from project_user)";
            try {
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                list.add(name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int queryDistinctBirthdayCnt() {
        String sql = "select count(distinct name) from project_user";
        int cnt = 0;
        try {
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            rs.next();
            cnt = rs.getInt("count");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cnt;
    }

    @Override
    public List<String> queryMostFollowersUserName() {
        List<String> list = new ArrayList<>();
        String sql = "select name from project_user " +
                "where mid = " +
                "(select user_mid as cnt from project_following " +
                "group by user_mid " +
                "having count(user_mid) =  " +
                "(select count(user_mid) from project_following " +
                "group by user_mid " +
                "order by count(user_mid) desc limit 1) " +
                ")";
        try {
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void insertUser(long mid, String name, String sex, String birth,
                           int lv, String sign, List<Long> fol, String idt) {
        String sql_user = "insert into project_user values(?, ?, ?, ?, ?, ?, ?)";
        try {
            stmt = conn.prepareStatement(sql_user);
            stmt.setLong(1, mid);
            stmt.setString(2, name);
            stmt.setString(3, sex);
            stmt.setString(4, birth);
            stmt.setInt(5, lv);
            stmt.setString(6, sign);
            stmt.setString(7, idt);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String sql_following = "insert into project_following values(?, ?)";
        try {
            stmt = conn.prepareStatement(sql_following);
            stmt.setLong(1, mid);
            for (int i = 0; i < fol.size(); i++) {
                stmt.setLong(2, fol.get(i));
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertFollowersByMID(long up_mid, long fans_mid) {
        String sql = "insert into project_following values(?, ?)";
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, up_mid);
            stmt.setLong(2, fans_mid);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateSexByMID(long mid, String sex) {
        String sql = "update project_user set sex = ? where mid = ?";
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setLong(2, mid);
            stmt.setString(1, sex);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteUserByMID(long mid) {
        String sql = "delete from project_user where mid = ?";
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, mid);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void concurrencyMulti() {
        Runnable[] runs = new UserThread[threadNum];
        Thread[] threads = new Thread[threadNum];
        long start = System.currentTimeMillis();
        for (int i = 0; i < threadNum; i++) {
            runs[i] = new UserThread(false);
            threads[i] = new Thread(runs[i]);
            threads[i].start();
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();
        //System.out.println("Time cost " + (end - start) + " ms.");
        System.out.println(end - start);
    }

    public void concurrencySingle() {
        Runnable run = new UserThread(true);
        Thread thread = new Thread(run);
        long start = System.currentTimeMillis();
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        //System.out.println("Time cost " + (end - start) + " ms.");
        System.out.println(end - start);
    }

    private class UserThread implements Runnable {
        List<Client.User> users;
        boolean single;

        public UserThread(boolean single) {
            this.single = single;
            Random random = new Random();
            users = new ArrayList<>();
            int turn = dataNum * (single ? threadNum : 1);
            for (int i = 0; i < turn; i++) {
                Client.User user = new Client.User();
                user.mid = ++cnt;
                user.name = Client.randName();
                user.sex = Client.randSex();
                user.birth = Client.randBirth();
                user.level = random.nextInt(7);
                user.sign = Client.randSign();
                user.fol = new ArrayList<>();
                user.idt = random.nextBoolean() ? "user" : "superuser";
                users.add(user);
            }
        }

        @Override
        public void run() {
            //System.out.println("run() execute");
            String sql = "insert into project_user values(?, ?, ?, ?, ?, ?, ?)";
            try {
                stmt = conn.prepareStatement(sql);
                for (int i = 0; i < dataNum * (single ? threadNum : 1); i++) {
                    Client.User user = users.get(i);
                    stmt.setLong(1, user.mid);
                    stmt.setString(2, user.name);
                    stmt.setString(3, user.sex);
                    stmt.setString(4, user.birth);
                    stmt.setInt(5, user.level);
                    stmt.setString(6, user.sign);
                    stmt.setString(7, user.idt);
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String queryByMid(long mid) {
        String res = null;
        String sql = "select * from project_user where mid = ?";
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, mid);
            rs = stmt.executeQuery();
            while (rs.next())
                res = rs.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }
}
