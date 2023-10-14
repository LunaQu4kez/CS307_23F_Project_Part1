



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
赵钊: Basic Comparison(including Report), Index & `B-Tree` `B+Tree` Comparison, Concurrency Test  
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
**toDo**: Finish the report mainly focus on 
(1) What tables have been designed and the reasons 
for this design (the reasons can be shorter) 
(2) What is the relationship between these tables 
(3) Why does this design conform to 3NF

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
We input our operation(an integer between `1` and `11`) id and if necessary, the script will generate random data, if not, it will run and calculate the time cost using file io or DBMS then print it out.**The time is not counted when `openDB()` and `closeDB()` is running.**

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
2. select name from project_user where ? <= mid & mid <= ?
3. select name from project_user where level = ?
4. select name from project_user where mid = ? -- ? is like '24%', '53%' and so on
```
We test each sentence for 5 times, record the result and visualize them with graph below. It is worth mentioning that in the second query, each time we change the range to see whether the program work good or not.


#### Queries Relating to Functions
We compare 2 queries in basic query, containing `max(unknown)` and `count(unknown)` in addition keyword `distinct`. The query sentences are below.
```sql
1. select name from project_user 
   where length(name) = (select max(length(name)) from project_user)
2. select count(distinct name) from project_user
```
Also, we test each sentence for 5 times, and the result are in the below graph.


#### Multi-table Query

There is a query relating to table `project_user` and `project_following` that return the `name` of user who has the most followers. And here is the query sentence.

```sql
select name from project_user where mid =
(select user_mid as cnt from project_following
group by user_mid having count(user_mid) =
(select count(user_mid) from project_following
group by user_mid order by count(user_mid) desc limit 1))
```



#### Insert
There are two insert sentence tested in this part, including insert an user and insert a follower to an user. The `DML` are as follow. The seven `?` in the first sentence respectively representing `mid`, `name`, `sex`, `birthday`, `level`, `sign` and `identity`. Also, the two `?` in the second sentence respectively representing `up_mid` and `fans_mid`. **Only when there is no data in buffer stream, the time of file io stops.**
```sql
1. insert into project_user values(?, ?, ?, ?, ?, ?, ?)
2. insert into project_following values(?, ?)
```




#### Update
We use the following `DML` sentence to test update operation. The two `?` respectively representing `sex` and `mid` this two `varchar` field.
```sql
update project_user set sex = ? where mid = ?
```




#### Delete
We use the following `DML` sentence to test delete operation. The `?` in the sentence means `mid` this field.
```sql
delete from project_user where mid = ?
```




### 5. Comparison between DBMS with Indexes and File I/O with B-Tree, B+Tree
According to the [`postgresql` docs](http://www.postgres.cn/docs/12/indexes-types.html), `postgresql` has an index system implemented by B-tree, which can increase the efficiency of some operations. So we can compare DBMS indexes with `Java` implemented B-tree and B+tree.




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

<p float="none">
  <img src="pic\\task4\\8_1.png" width="420" />
  <img src="pic\\task4\\8_2.png" width="420" />
</p>
