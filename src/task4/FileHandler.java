package task4;

import java.io.*;
import java.util.*;

public class FileHandler implements DataHandler {
    private String filePath = "data\\users-1.csv";
    private BTree<Long, String> infoTree;

    @Override
    public List<String> queryAllUsersName() {
        List<String> list = new ArrayList<>();
        int nameIdx = 1;
        String line;
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader bf = new BufferedReader(new FileReader(filePath));
            bf.readLine();
            while ((line = bf.readLine()) != null) {
                sb.append(line);
                if (line.length() >= 4 && line.substring(line.length() - 4).equals("user")) {
                    list.add(sb.toString().split(",")[nameIdx]);
                    sb = new StringBuilder();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("size = " + list.size());
        return list;
    }

    @Override
    public List<String> queryNameByMIDRange(long min, long max) {
        List<String> list = new ArrayList<>();
        int midIdx = 0, nameIdx = 1;
        String line;
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader bf = new BufferedReader(new FileReader(filePath));
            bf.readLine();
            while ((line = bf.readLine()) != null) {
                sb.append(line);
                if (line.length() >= 4 && line.substring(line.length() - 4).equals("user")) {
                    String[] data = sb.toString().split(",");
                    long mid = Long.parseLong(data[midIdx]);
                    if (min <= mid && mid <= max) list.add(data[nameIdx]);
                    sb = new StringBuilder();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("size = " + list.size());
        return list;
    }

    @Override
    public List<String> queryNameByLevel(int level) {
        List<String> list = new ArrayList<>();
        int nameIdx = 1, cnt = 0;
        String line;
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader bf = new BufferedReader(new FileReader(filePath));
            bf.readLine();
            while ((line = bf.readLine()) != null) {
                sb.append(line);
                if (line.length() >= 4 && line.substring(line.length() - 4).equals("user")) {
                    cnt++;
                    String[] data = sb.toString().split(",");
                    int lvIdx = 0;
                    while (true) {
                        if (data[lvIdx].equals("保密") || data[lvIdx].equals("男") || data[lvIdx].equals("女")) break;
                        lvIdx++;
                    }
                    lvIdx += 2;
                    int lv = Integer.parseInt(data[lvIdx]);
                    if (lv == level) list.add(data[nameIdx]);
                    sb = new StringBuilder();
                }
            }
        } catch (Exception e) {
            System.out.println("cnt = " + cnt);
            e.printStackTrace();
        }
        System.out.println("size = " + list.size());
        return list;
    }

    @Override
    public List<String> queryNameByMID2Digits(char c1, char c2) {
        List<String> list = new ArrayList<>();
        int midIdx = 0, nameIdx = 1;
        String line;
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader bf = new BufferedReader(new FileReader(filePath));
            bf.readLine();
            while ((line = bf.readLine()) != null) {
                sb.append(line);
                if (line.length() >= 4 && line.substring(line.length() - 4).equals("user")) {
                    String[] data = sb.toString().split(",");
                    String mid = data[midIdx];
                    if (mid.charAt(0) == c1 && mid.charAt(1) == c2) list.add(data[nameIdx]);
                    sb = new StringBuilder();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("size = " + list.size());
        return list;
    }

    @Override
    public List<String> queryLongestName() {
        List<String> list = new ArrayList<>();
        int cnt = 0, maxLen = 0;
        Set<String> names = new HashSet<>();
        String line;
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader bf = new BufferedReader(new FileReader(filePath));
            bf.readLine();
            while ((line = bf.readLine()) != null) {
                sb.append(line);
                if (line.length() >= 4 && line.substring(line.length() - 4).equals("user")) {
                    cnt++;
                    String[] data = sb.toString().split(",");
                    int sexIdx = 2;
                    while (true) {
                        if (data[sexIdx].equals("保密") || data[sexIdx].equals("男") || data[sexIdx].equals("女")) break;
                        sexIdx++;
                    }
                    StringBuilder name = new StringBuilder();
                    for (int i = 1; i < sexIdx; i++) {
                        name.append(data[i]);
                    }
                    maxLen = Math.max(name.toString().length(), maxLen);
                    names.add(name.toString());
                    sb = new StringBuilder();
                }
            }
        } catch (Exception e) {
            System.out.println("cnt = " + cnt);
            e.printStackTrace();
        }
        for (String name: names)
            if (name.length() == maxLen)
                list.add(name);
        System.out.println("size = " + list.size());
        return list;
    }

    @Override
    public int queryDistinctBirthdayCnt() {
        int cnt = 0;
        Set<String> set = new HashSet<>();
        String line;
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader bf = new BufferedReader(new FileReader(filePath));
            bf.readLine();
            while ((line = bf.readLine()) != null) {
                sb.append(line);
                if (line.length() >= 4 && line.substring(line.length() - 4).equals("user")) {
                    cnt++;
                    String[] data = sb.toString().split(",");
                    int birthIdx = 2;
                    while (true) {
                        if (data[birthIdx].equals("保密") || data[birthIdx].equals("男")
                                || data[birthIdx].equals("女"))
                            break;
                        birthIdx++;
                    }
                    birthIdx++;
                    if (!data[birthIdx].equals("")) {
                        String birth = data[birthIdx];
                        if (!data[birthIdx].endsWith("日")) {
                            birth = Integer.parseInt(birth.split("-")[0]) + "月" +
                                    Integer.parseInt(birth.split("-")[1]) + "日";
                        }
                        set.add(birth);
                    }
                    sb = new StringBuilder();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return set.size();
    }

    @Override
    public List<String> queryMostFollowersUserName() {
        List<String> list = new ArrayList<>();
        int cnt = 0, maxFol = 0, nameIdx = 1;
        Map<String, Integer> map = new HashMap<>();
        String line;
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader bf = new BufferedReader(new FileReader(filePath));
            bf.readLine();
            while ((line = bf.readLine()) != null) {
                sb.append(line);
                if (line.length() >= 4 && line.substring(line.length() - 4).equals("user")) {
                    cnt++;
                    String[] data = sb.toString().split("\"");
                    int folNum = 0;
                    if (data.length > 1) {
                        String fol = data[data.length - 2];
                        folNum = fol.split(",").length;
                    }
                    maxFol = Math.max(maxFol, folNum);
                    data = sb.toString().split(",");
                    map.put(data[nameIdx], folNum);

                    sb = new StringBuilder();
                }
            }
        } catch (Exception e) {
            System.out.println("cnt = " + cnt);
            e.printStackTrace();
        }
        for (String name : map.keySet())
            if (map.get(name) == maxFol)
                list.add(name);
        System.out.println("size = " + list.size());
        System.out.println("maxFol = " + maxFol);
        return list;
    }

    @Override
    public void insertUser(long mid, String name, String sex, String birth,
                           int lv, String sign, List<Long> fol, String idt) {
        StringBuilder sb = new StringBuilder();
        sb.append(mid);
        sb.append(",");
        sb.append(name);
        sb.append(",");
        sb.append(sex);
        sb.append(",");
        sb.append(birth);
        sb.append(",");
        sb.append(lv);
        sb.append(",");
        sb.append(sign);
        sb.append(",");
        if (fol.size() == 0) {
            sb.append("[],");
        } else {
            sb.append("\"[");
            sb.append("'" + fol.get(0) + "'");
            for (int i = 1; i < fol.size(); i++) {
                sb.append(", '");
                sb.append(fol.get(i));
                sb.append("'");
            }
            sb.append("]\",");
        }
        sb.append(idt);
        sb.append("\n");
        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertFollowersByMID(long up_mid, long fans_mid) {
        List<String> list = new ArrayList<>();
        String line;
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader bf = new BufferedReader(new FileReader(filePath));
            list.add(bf.readLine() + "\n");
            while ((line = bf.readLine()) != null) {
                sb.append(line);
                if (line.length() >= 4 && line.substring(line.length() - 4).equals("user")) {
                    String[] data = sb.toString().split("\"");
                    if (Long.parseLong(sb.toString().split(",")[0]) == up_mid) {
                        if (data[data.length - 1].equals(",user") || data[data.length - 1].equals(",superuser")) {
                            data[data.length - 2] = data[data.length - 2].substring(1, data[data.length - 2].length() - 1);
                            data[data.length - 2] += ", '" + fans_mid + "'";
                            data[data.length - 2] = "\"[" + data[data.length - 2] + "]\"";
                            sb = new StringBuilder();
                            for (String str : data) {
                                sb.append(str);
                            }
                        } else {
                            data = sb.toString().split("]");
                            sb = new StringBuilder();
                            for (int i = 0; i < data.length - 2; i++) {
                                sb.append(data[i]);
                            }
                            sb.append(data[data.length - 2].substring(0, data[data.length - 2].length() - 1));
                            sb.append("\"['");
                            sb.append(fans_mid);
                            sb.append("']\"");
                            sb.append(data[data.length - 1]);
                        }
                    }
                    sb.append("\n");
                    list.add(sb.toString());
                    sb = new StringBuilder();
                } else {
                    sb.append("\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("size = " + list.size());
        try (FileWriter writer = new FileWriter(filePath)) {
            for (int i = 0; i < list.size(); i++) {
                writer.write(list.get(i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateSexByMID(long mid, String sex) {
        List<String> list = new ArrayList<>();
        String line;
        StringBuilder sb = new StringBuilder();
        int midIdx = 0;
        try {
            BufferedReader bf = new BufferedReader(new FileReader(filePath));
            list.add(bf.readLine() + "\n");
            while ((line = bf.readLine()) != null) {
                sb.append(line);
                if (line.length() >= 4 && line.substring(line.length() - 4).equals("user")) {
                    String[] data = sb.toString().split(",");
                    if (Long.parseLong(data[midIdx]) == mid) {
                        int sexIdx = 2;
                        while (true) {
                            if (data[sexIdx].equals("保密") || data[sexIdx].equals("男")
                                    || data[sexIdx].equals("女")) break;
                            sexIdx++;
                        }
                        data[sexIdx] = sex;
                        sb = new StringBuilder();
                        for (int i = 0; i < data.length - 1; i++) {
                            sb.append(data[i]);
                            sb.append(",");
                        }
                        sb.append(data[data.length - 1]);
                    }
                    sb.append("\n");
                    list.add(sb.toString());
                    sb = new StringBuilder();
                } else {
                    sb.append("\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("size = " + list.size());
        try (FileWriter writer = new FileWriter(filePath)) {
            for (int i = 0; i < list.size(); i++) {
                writer.write(list.get(i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteUserByMID(long mid) {
        List<String> list = new ArrayList<>();
        String line;
        StringBuilder sb = new StringBuilder();
        int midIdx = 0;
        try {
            BufferedReader bf = new BufferedReader(new FileReader(filePath));
            list.add(bf.readLine() + "\n");
            while ((line = bf.readLine()) != null) {
                sb.append(line);
                if (line.length() >= 4 && line.substring(line.length() - 4).equals("user")) {
                    String[] data = sb.toString().split(",");
                    if (Long.parseLong(data[midIdx]) != mid) {
                        sb.append("\n");
                        list.add(sb.toString());
                    }
                    sb = new StringBuilder();
                } else {
                    sb.append("\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("size = " + list.size());
        try (FileWriter writer = new FileWriter(filePath)) {
            for (int i = 0; i < list.size(); i++) {
                writer.write(list.get(i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getBTree() {
        infoTree = new BTree<>();
        int midIdx = 0;
        String line;
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader bf = new BufferedReader(new FileReader(filePath));
            bf.readLine();
            while ((line = bf.readLine()) != null) {
                sb.append(line);
                if (line.length() >= 4 && line.substring(line.length() - 4).equals("user")) {
                    long mid = Long.parseLong(sb.toString().split(",")[midIdx]);
                    infoTree.put(mid, sb.toString());
                    sb = new StringBuilder();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String queryByMid(long mid) {
        return infoTree.get(mid);
    }
}
