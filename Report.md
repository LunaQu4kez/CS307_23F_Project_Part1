



# CS307 Project Part I

## Basic Information
|  Member   | Student ID  | Contribution Rate |
|  ----  | ----  | ---- |
| 袁龙  | 12211308 | 33.33%   |
| 于斯瑶  | 12211655 |  33.33% |
| 赵钊  | 12110120 |  33.33%  |

**Contribution of work** 
袁龙: E-R Diagram, Table Creation(including Report), `postgresql` & `MySQL` Comparison 
于斯瑶: Data Import and Optimize(including Report), `JDBC` & `PDBC` Comparison 
赵钊: Basic Comparison(including Report), Index & `B-Tree`  Comparison, Concurrency Test  
Together: Database Design

## Task 1: E-R Diagram
We use [Visio](https://visio.iruanhui.cn/?bd_vid=9042954634973670880) to draw the E-R diagram.
<p float="none">
  <img src="pic\\task1\\E-R diagram.png" width="840" />
</p>








## Task 2: Database Design

### 1. E-R Diagram By DataGrip
<p float="none">
  <img src="pic\\task2\\database diagram.png" width="840" />
</p>

### 2. Table Design Description
By analyzing the initial data, we gained some key information:  
* "following" in user.csv usually has multiple mids of up.
* "like", "coin", "favorite" and "view" in videos.csv usually has mutiple mids.
* In the same video, the same person can send identical danmu at the same time.  

This information results in tables that do not satisfy the three normal forms.
In order to make the database design meet the three normal forms and be as easy to expand as possible, 
we designed the following tables. 

**project_user**  
* `mid`: the uid of the user.
* `name`: the name of the user.
* `sex`: the sex of the user.
* `birthday`: the birthday of the user.
* `level`: the level of the user.
* `sign`: the personal description.
* `identity`: "user" or "superuser".
  
**project_following**
* `up_mid`: the mid of up followed by fans.
* `fans_mid`: the mid of the user following the up.
  
**project_video**
* `BV`: the unique identification string of a video.
* `title`: the name of the video.
* `owner_mid`: the mid of the video owner.
* `commit_time`: the time when the owner committed this video.
* `review_time`: the time when the video was inspected by its reviewer
* `public_time`: the time when the video was made public for all users.
* `duration`: the video duration.
* `description`: the brief text introduction given by the uploader.
* `reviewr`: the mid of the video reviewer.

**project_like**
* `BV`: the video BV of the video liked.
* `mid`: the mid of the user who liked the video.
  
**project_coin**
* `BV`: the video BV of the video given the coins.
* `mid`: the mid of the user who gave the coins to the video. 

**project_favorite**
* `BV`: the video BV of the video favorited.
* `mid`: the mid of the user who favorited the video.

**project_view**
* `BV`: the video BV of the video watched.
* `mid`: the mid of the user who watched the video.
* `time`: last watch time duration of the user.

**project_danmu**
* `id`: the tag of danmu. (Increment primary key)
* `BV`: the video BV of the video that the Danmu was sent.
* `mid`: the mid of the user who sent the Danmu.
* `time`: the time of the video that Danmu appears.
* `content`: the content of the Danmu.  
  
Our design satisfies the need of following:
* Follow the requirements of the three normal forms.
  * Each property cannot contain multiple values or duplicate values.
  * Every non-key attribute (column) is fully functionally dependent on the entire primary key.
  * Each non-primary key property does not depend on other non-primary key properties.

* Each table has its primary key.
* Every table is included in a link. No isolated tables included.
* Contain no circular links.
* Each table has its own NOTNULL column and UNIQUE column.
* Use appropriate types for different fields of data.

### 3. Scalability
The tables we've designed exhibit strong scalability:  
* "project_following" can store all following ups' mid of users. 
It's also very easy to add or delete new following. 
Meanwhile, It can easily retrieve the list of users following this user.
* "project_like", "project_coin" and "project_favorite" also have the scalability like "project_following".
* "project_view" uses the composite primary key, which can make update the time easier.

## Task 3: Data Import






## Task 4: Compare DBMS with File I/O
### 1. Test Environment
**Hardware specification** 
CPU: 11th Gen Intel(R) Core(TM) i5-11300H @ 3.10GHz  
RAM: 16.0 GB  
**Software specification** 
DBMS: `postgresql-12.5`  
OS: Windows 10 Home Chinese Version  
Programing language: `Java 13.0.2`, `python 3.10.9`   
IDE: `IntelliJ IDEA 2018.3.3` for Java, `Pycharm Community Edition 2021.3.1` for python 
Additional libraries: `postgresql-42.6.0.jar`   
**The result of tests may be different in different test environment.**

In order to replicate the experiment, you may need to construct the project 
structural components as follows.

```
CS307_23F_Project_Part1
│
├── data
│   │   users.csv
│   │   videos.csv
│   └── danmu.csv
│
├── properties
│   └── jdbc.properties
│
└── src
    └── task4
        └── *.java
```



### 2. Test Description

In this task, we mainly use the data in file `user.csv`, so we mainly focus on table `project_user` and `project_follow`. In the basic part, we will test some DQL and some DML including insert, update, delete operations then compare them with `java.io`. In the advanced task, we will compare DBMS with indexes with Java implemented `B-tree`, `B+tree`; using multi-user and multi-threading to test concurrency; comparing `postgresql` with `MySQL`; comparing `JDBC` with `Python Database Connection`.



### 3. Script Description

There are mainly 4 java file we used in finishing the basic part.
#### DataHandler.java
This is an interface which contains some methods that will be implemented in class `FileHandler` and `DatabaseHandler`.The methods are listed below. There are very detailed annotation in the source code that explained every method.
```java
public interface DataHandler {
    public List<String> queryAllUsersName();
    public List<String> queryNameByMIDRange(long min, long max);
    public List<String> queryNameByLevel(int level);
    public List<String> queryNameByMID2Digits(char c1, char c2);
    public List<String> queryLongestName();
    public int queryDistinctBirthdayCnt();
    public List<String> queryMostFollowersUserName();
    public void insertUser(long mid, String name, String sex, String birth, 
                           int lv, String sign, List<Long> fol, String idt);
    public void insertFollowersByMID(long up_mid, long fans_mid);
    public void updateSexByMID(long mid, String sex);
    public void deleteUserByMID(long mid);
}
```

#### FileHandler.java
This is a script only use `java.io` and `java.util` to implements the method in the interface `DataHandler`. Instead of `DBMS` operations, we use a lot of String operations such as `split()`, `substring()` and `equals()` and some collections such as `Map<>`(implemented by `HashMap<>`), `HashSet<>` (implemented by `HashSet<>`) and `List<>`(implemented by `ArrayList<>`).

#### DatabaseHandler.java
Despite the methods implemented from the interface `DataHandler`, there are two additional method called `openDB()` and `closeDB()` which means get and close the connection between program and database.

#### Client.java
This is the class that we run our script so it is called `Client`. The `main` method in this class is below. 
```java
public class Client {
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
}
```
We input our operation(an integer between `1` and `11`) id and if necessary, the script will generate random data, if not, it will run and calculate the time cost using file I/O or DBMS then print it out. **The time is not counted when `openDB()` and `closeDB()` is running. Similarly, the time of File I/O is only counted when doing operations on file.**

#### jdbc.properties
A property file that contains some necessary information to connect to DBMS. Information contains 
- `driver`: The class name for `postgresql` driver
- `ip`: The user ip which default value is `localhost`
- `port`: The port of `postgresql`
- `db_name`: The database name we will connect
- `user`: The user's name which default value is `postgres`
- `password`: The password of the user



### 4. Basic Comparison

#### Basic Queries
We compare 4 queries in basic query, containing query all, range query, specific query and vague query. The query sentences are below.
```sql
1. select name from project_user
2. select name from project_user where ? <= mid and mid <= ?
3. select name from project_user where level = ?
4. select name from project_user where mid = ? -- ? is like '24%', '53%' and so on
```
**We test each sentence for 5 times**, record the result and visualize them with graph below. It is worth mentioning that in the second query, each time we change the range to see whether the program work good or not. For query 2 to 4, each time we change the value of `?` to wider the testing range. And the result of this 4 basic query are as follows.

<p float="none">
  <img src="pic\\task4\\4_1.1.png" width="800" />
</p>

<p float="none">
  <img src="pic\\task4\\4_1.2.png" width="800" />
</p>

**We have found that in all four queries, DBMS does better than File I/O. In addition, we observed that in range query, the larger the range is, the more time both File I/O and DBMS cost. However, the growth percentage of File I/O is larger.** This can be explain from the underlying implementation. Every time, File I/O will iterate every line, which complexity is `O(N)`. But DBMS may use B-Tree to manage data, with a complexity of `O(logN)`.



#### Queries Relating to Functions

We compare 2 queries in basic query, containing `max(unknown)` and `count(unknown)` in addition keyword `distinct`. The query sentences are below.
```sql
1. select name from project_user 
   where length(name) = (select max(length(name)) from project_user)
2. select count(distinct name) from project_user
```
Also, we test each sentence for 5 times, and the result are in the below graph. **In the implement of function `max()`, DBMS is better than File I/O. But in the implement of keyword `distinct`, File I/O perform better. This may because `HashSet<>` is very efficiency since its complexity in single operation is `O(1)`.**

<p float="none">
  <img src="pic\\task4\\4_2.png" width="800" />
</p>





#### Multi-table Query

There is a query relating to table `project_user` and `project_following` that return the `name` of user who has the most followers. And here is the query sentence.

```sql
select name from project_user 
group by mid order by count(mid) desc limit 1
```

The result is as follows. We can find that: **The efficiency of DBMS is not affected by the increase in the number of data tables. **Maybe DBMS has efficient way to join multi-table together.

<p float="none">
  <img src="pic\\task4\\4_3.png" width="400" />
</p>



#### Insert
There are two insert sentence tested in this part, including insert an user and insert a follower to an user. The `DML` are as follow. The seven `?` in the first sentence respectively representing `mid`, `name`, `sex`, `birthday`, `level`, `sign` and `identity`. Also, the two `?` in the second sentence respectively representing `up_mid` and `fans_mid`. **Only when there is no data in buffer stream, the time of file io stops.**
```sql
1. insert into project_user values(?, ?, ?, ?, ?, ?, ?)
2. insert into project_following values(?, ?)
```

All inserted data is generate randomly by methods in class `Client`.  **The data size of this two test are both 1000**, that is, we will test 5 times and each time we insert 1000 random data into the corresponding table. The result of this two insert sentence test is below. 

<p float="none">
  <img src="pic\\task4\\4_4.png" width="800" />
</p>

**We find that in the first inert operation, File I/O does better than DBMS. This may because if we want to insert an `user`, we just need to append a single line at the end of the file and this is very fast. But if we want to add a follower to one user, we need to rewrite the whole file. And in the two insert operations, DBMS takes about the same amount of time which reflect DBMS Has good stability.**




#### Update
We use the following `DML` sentence to test update operation. The two `?` respectively representing `sex` and `mid` this two `varchar` field.
```sql
update project_user set sex = ? where mid = ?
```

All updated data is generate randomly by methods in class `Client`.  **Each time we update 500 users `sex` and we test for 5 times.** The result of this test is below. **File I/O cost much more time than DBMS. This is similarly to the second insert operation, if we want to update an user's `sex` we need to rewrite the whole file which cost more time.** 

<p float="none">
  <img src="pic\\task4\\4_5.png" width="400" />
</p>




#### Delete
We use the following `DML` sentence to test delete operation. The `?` in the sentence means `mid` this field.
```sql
delete from project_user where mid = ?
```

The user we delete each time is chosen randomly and the users deleted in file and database in one test are the same. **Each time we delete 500 users and we test for 5 times.** The result of this test is as follows. **The same as insert and update, the delete operation of File I/O is far more slower than DBMS.** This is because if we want to delete a data with File I/O, we need to rewrite the whole file, which cost a large amount of time. 

<p float="none">
  <img src="pic\\task4\\4_6.png" width="400" />
</p>




### 5. Comparison between DBMS with Indexes and File I/O with B-Tree
According to the [`postgresql` docs](http://www.postgres.cn/docs/12/indexes-types.html), `postgresql` has an index system implemented by B-tree, which can increase the efficiency of some operations. So we can compare DBMS indexes with `Java` implemented B-tree.

In this part, we compare the index of the primary key in `project_user` and File I/O with B-Tree. Since the the primary key of a table has native index, we do not need to add extra index to the table. 

The `Java` implemented B-tree is in `BTree.java`. The implementation is refer to Algorithms, 4th Edition by Robert Sedgewick and Kevin Wayne. Two inner class `Node` and `Entry` are used to construct the B-Tree. There are two `public` access permission method as follows which we can call them to insert and find data.

```java
public class BTree<Key extends Comparable<Key>, Value>  {    
	private static final class Node {
        private int m;
        private Entry[] children;

        private Node(int k) {
            m = k;
            children = new Entry[M];
        }
    }

    private static class Entry {
        private Comparable key;
        private Object val;
        private Node next;

        public Entry(Comparable key, Object val, Node next) {
            this.key  = key;
            this.val  = val;
            this.next = next;
        }
    }
    
    public Value get(Key key);
    public void put(Key key, Value val);
}
```

In this test, we compare query between DBMS and File I/O. **We test for 5 times and each time we will randomly query 1000 users' `mid` to get their `name`. ** Here are the query sentence and the result is as follows.

```sql
select * from project_user where mid = ?
```

<p float="none">
  <img src="pic\\task4\\5.png" width="400" />
</p>

We may find that in this case File I/O with B-Tree cost much less time than without B-Tree and **it is even fast than DBMS**. We conclude that although the build of B-Tree may cost some time, when the number of query operations increase, the advantage of B-Tree will be greater.




### 6. Comparison between Different DBMS
In this part, we will compare `postgresql` with `MySQL` in single table query, insert, update and delete.




### 7. Comparison between Different Language Connecting Database
We will mainly compare the efficiency of `JDBC` and `PDBC` in this part including connecting, query, insert, disconnecting this four part.




### 8. Concurrency Test
To test the concurrency of our DBMS, we use multi-thread to simultaneously perform insert operations on the DBMS. There are two method added in class `DatabaseHandler` which are `concurrencyMulti()` and `concurrencySingle()`, respectively representing multi-thread and single-thread insertion. Then a new client is created called `ConcurrencyTest.java` that has been pasted below.
```java
public class ConcurrencyTest {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int opt = sc.nextInt();
        DatabaseHandler dh = new DatabaseHandler();
        dh.openDB();
        switch (opt) {
            case 1:
                dh.concurrencySingle();
                break;
            case 2:
                dh.concurrencyMulti();
                break;
        }
        dh.closeDB();
    }
}
```
We set the total number of data is `100000`, and use `1`,  `5`,  `10`,  `20`,  `50` threads to test the time cost. We test each condition for `5` times and the result is the average of them. 

The result is as follows. The figure on the left shows time cost of each number of threads (we called this one condition) and the x-axis means the first to the fifth time we tested. It is easy to figure out that the **average time of each condition is almost equal**, although more threads cost a bit more time. We also observed that **single thread is more instable**, with the lowest time cost and highest time cost among all. **So we conclude that the concurrency of the DBMS is good because more threads will not significantly decrease its efficiency.** 

The figure on the right is a line chart which shows the time cost of different condition deal with different size of data. From the graph, **all condition are extremely close to linear**, with single thread cost just a bit less time. **This means even more threads and more data, DBMS will not cost extra time so the concurrency of DBMS is good.**

<p align="middle">
  <img src="pic\\task4\\8.png" width="1000" />
</p>

