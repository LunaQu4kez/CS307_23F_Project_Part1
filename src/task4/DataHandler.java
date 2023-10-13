package task4;

import java.util.List;

public interface DataHandler {


    // Single Table Query
    /**
     * Query all
     * @return  A list of all users' name.
     */
    public List<String> queryAllUsersName();

    /**
     * Range Query
     * @param min  minimum mid
     * @param max  maximum mid
     * @return  A list of users' name whose mid are in [min, max].
     */
    public List<String> queryNameByMIDRange(long min, long max);

    /**
     * Specific Query
     * @param level  target level
     * @return  A list of users' name whose level is level.
     */
    public List<String> queryNameByLevel(int level);

    /**
     * Vague Query
     * @param c1  first digit
     * @param c2  second digit
     * @return  A list of users' name whose mid's first two character is c1, c2
     */
    public List<String> queryNameByMID2Digits(char c1, char c2);

    /**
     * Max Query
     * @return  A list of user's name whose names are the longest.
     */
    public List<String> queryLongestName();

    /**
     * Count Query
     * @return  Number of distinct birthday.
     */
    public int queryDistinctBirthdayCnt();



    // Multi-table Query
    /**
     * @return  Name of followers who have the most followers.
     */
    public List<String> queryMostFollowersUserName();



    // Insert
    /**
     * Insert one user.
     * @param mid  The mid of the user.
     * @param name  The name of the user.
     * @param sex  The sex of the user which can only be "男", "女" or "保密".
     * @param birth  The birthday of the user.
     * @param lv  The level(from 0 to 6) of the user.
     * @param sign  The signature of the user.
     * @param fol  The list of followers mid of the user.
     * @param idt  The identity of the user which can only be "user" or "superuser"
     */
    public void insertUser(long mid, String name, String sex, String birth,
                           int lv, String sign, List<Long> fol, String idt);

    /**
     * Insert a data which is an user follow an user.
     * @param up_mid  The mid of the user being followed.
     * @param fans_mid  The mid of the user following others.
     */
    public void insertFollowersByMID(long up_mid, long fans_mid);



    // Update
    /**
     * An user update his/her sex.
     * @param mid  The mid of the user update sex
     * @param sex  The sex will be update
     */
    public void updateSexByMID(long mid, String sex);



    // Delete
    /**
     * Delete an user.
     * @param mid  the mid of the user
     */
    public void deleteUserByMID(long mid);

}
