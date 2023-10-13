package task4;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Client {
    private static String filePath = "data\\users-1.csv";
    private static Set<Long> mids;
    private static List<User> users;
    private static final int usersLen = 50000;
    private static List<long[]> follows;
    private static final int followsLen = 5000;
    private static Map<Long, String> sexes;
    private static final int sexesLen = 5;
    private static List<Long> del;
    private static final int delLen = 2;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        FileHandler fh = new FileHandler();
        DatabaseHandler dh = new DatabaseHandler();
        int opt = sc.nextInt();
        if (8 <= opt && opt <= 11) getMid();
        if (opt == 8) randomGenerateUser();
        else if (opt == 9) randomFollow();
        else if (opt == 10) randomUpdateSex();
        else if (opt == 11) randomDeleteUser();
        fileTest(fh, opt);
        dbTest(dh, opt);
    }

    private static void fileTest(FileHandler fh, int opt) {
        dataTest(fh, opt);
    }

    private static void dbTest(DatabaseHandler dh, int opt) {
        dh.openDB();
        dataTest(dh, opt);
        dh.closeDB();
    }

    private static void dataTest(DataHandler handler, int opt) {
        long start, end;
        start = System.currentTimeMillis();
        switch (opt) {
            case 1:
                handler.queryAllUsersName();
                break;
            case 2:
                handler.queryNameByMIDRange(100000, 5000000);  // Can change min, max
                break;
            case 3:
                handler.queryNameByLevel(1);  // Can change level
                break;
            case 4:
                handler.queryNameByMID2Digits('2', '6');  // Can change c1, c2
                break;
            case 5:
                handler.queryLongestName();
                break;
            case 6:
                handler.queryDistinctBirthdayCnt();
                break;
            case 7:
                handler.queryMostFollowersUserName();
                break;
            case 8:
                for (User user : users)
                    handler.insertUser(user.mid, user.name, user.sex, user.birth,
                            user.level, user.sign, user.fol, user.idt);
                break;
            case 9:
                for (long[] data: follows)
                    handler.insertFollowersByMID(data[0], data[1]);
                break;
            case 10:
                for (long mid : sexes.keySet())
                    handler.updateSexByMID(mid, sexes.get(mid));
                break;
            case 11:
                for (long mid : del)
                    handler.deleteUserByMID(mid);
                break;
        }
        end = System.currentTimeMillis();
        System.out.println("Time spend " + (end - start) + " ms.");
    }

    static class User {
        long mid;
        String name, sex, birth;
        int level;
        String sign;
        List<Long> fol;
        String idt;
    }

    private static void randomGenerateUser() {
        users = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < usersLen; i++) {
            User user = new User();
            user.mid = randMid();
            mids.add(user.mid);
            user.name = randName();
            user.sex = randSex();
            user.birth = randBirth();
            user.level = random.nextInt(7);
            user.sign = randSign();
            user.fol = new ArrayList<>();
            user.idt = random.nextBoolean() ? "user" : "superuser";
            users.add(user);
        }
    }

    private static long randMid() {
        Random random = new Random();
        long mid;
        while (mids.contains(mid = random.nextInt((int) 1e9)));
        return mid;
    }

    private static String randName() {
        Random random = new Random();
        StringBuilder name = new StringBuilder();
        int len = random.nextInt(12) + 4;
        for (int i = 0; i < len; i++) {
            name.append((char)(48 + random.nextInt(75)));
        }
        return name.toString();
    }

    private static String randSex() {
        Random random = new Random();
        String[] sex = new String[]{"男", "女", "保密"};
        return sex[random.nextInt(3)];
    }

    private static String randBirth() {
        Random random = new Random();
        int day;
        int month = 1 + random.nextInt(12);
        if (month == 2)
            day = 1 + random.nextInt(29);
        else if (month == 1 || month == 3 || month == 5 || month == 7
                || month == 8 || month == 10 || month == 12)
            day = 1 + random.nextInt(31);
        else
            day = 1 + random.nextInt(30);
        return month + "月" + day + "日";
    }

    private static String randSign() {
        Random random = new Random();
        StringBuilder sign = new StringBuilder();
        int len = random.nextInt(60);
        for (int i = 0; i < len; i++) {
            sign.append((char)(48 + random.nextInt(75)));
        }
        return sign.toString();
    }

    private static void randomFollow() {
        follows = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < followsLen; i++) {
            long[] data = new long[2];
            int cnt = 0;
            int idx = random.nextInt(mids.size());
            for (long up_mid : mids) {
                if (cnt++ == idx) {
                    data[0] = up_mid;
                    break;
                }
            }
            while (data[1] < 1 || data[1] > (long)3e15)
                data[1] = random.nextLong();
            follows.add(data);
        }
    }

    private static void randomUpdateSex() {
        sexes = new HashMap<>();
        Random random = new Random();
        for (int i = 0; i < sexesLen; i++) {
            long key = 0;
            int idx = random.nextInt(mids.size());
            int cnt = 0;
            for (long mid : mids) {
                if (cnt++ == idx) {
                    key = mid;
                    break;
                }
            }
            sexes.put(key, randSex());
        }
    }

    private static void randomDeleteUser() {
        del = new ArrayList<>();
        int cnt = 0;
        for (long mid : mids) {
            del.add(mid);
            if (++cnt > delLen) break;
        }
        for (long mid : del) {
            mids.remove(mid);
        }
    }

    private static void getMid() {
        mids = new HashSet<>();
        int midIdx = 0;
        String line;
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader bf = new BufferedReader(new FileReader(filePath));
            bf.readLine();
            while ((line = bf.readLine()) != null) {
                sb.append(line);
                if (line.length() >= 4 && line.substring(line.length() - 4).equals("user")) {
                    mids.add(Long.parseLong(sb.toString().split(",")[midIdx]));
                    sb = new StringBuilder();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
