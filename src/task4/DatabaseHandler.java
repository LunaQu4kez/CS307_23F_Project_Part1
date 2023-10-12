package task4;

import java.util.List;

public class DatabaseHandler implements DataHandler {
    @Override
    public List<String> queryAllUsersName() {
        return null;
    }

    @Override
    public List<String> queryNameByMIDRange(long min, long max) {
        return null;
    }

    @Override
    public List<String> queryNameByLevel(int level) {
        return null;
    }

    @Override
    public List<String> queryNameByMID2Digits(char c1, char c2) {
        return null;
    }

    @Override
    public List<String> queryLongestName() {
        return null;
    }

    @Override
    public int queryDistinctBirthdayCnt() {
        return 0;
    }

    @Override
    public List<String> queryMostFollowersUserName() {
        return null;
    }

    @Override
    public void insertUser(long mid, String name, String sex, String birth, int lv, String sign, List<Long> fol, String idt) {

    }

    @Override
    public void updateSexByMID(long mid, String sex) {

    }

    @Override
    public void updateFollowersByMID(long up_mid, long fans_mid) {

    }

    @Override
    public void deleteUserByMID(long mid) {

    }
}
