package task4;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class IndexTest {
    private static Set<Long> mids;
    private static String filePath = "data\\users-1.csv";
    private static int time = 1000;
    private static long[] testData = new long[time];

    public static void main(String[] args) {
        getMid();
        for (int i = 0; i < time; i++) {
            testData[i] = randMid();
        }
        FileHandler fh = new FileHandler();
        DatabaseHandler dh = new DatabaseHandler();
        fileTest(fh);
        dbTest(dh);
    }

    private static void fileTest(FileHandler fh) {
        long start = System.currentTimeMillis();
        fh.getBTree();
        for (long mid : testData) fh.queryByMid(mid);
        long end = System.currentTimeMillis();
        //System.out.println("Time cost " + (end - start) + " ms.");
        System.out.println(end - start);
    }

    private static void dbTest(DatabaseHandler dh) {
        dh.openDB();
        long start = System.currentTimeMillis();
        for (long mid : testData) dh.queryByMid(mid);
        long end = System.currentTimeMillis();
        //System.out.println("Time cost " + (end - start) + " ms.");
        System.out.println(end - start);
        dh.closeDB();
    }

    private static long randMid() {
        Random random = new Random();
        int idx = random.nextInt(mids.size()), cnt = 0;
        long mid = 0;
        for (long x : mids) {
            mid = x;
            if (cnt++ == idx) break;
        }
        return mid;
    }

    public static void getMid() {
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
