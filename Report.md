# CS307 Project Part I
## Basic Information
|  Member   | Student ID  | Contribution Rate |
|  ----  | ----  | ---- |
| 袁龙  | 12211308 | 33.33%   |
| 于斯瑶  | 12211655 |  33.33% |
| 赵钊  | 12110120 |  33.33%  |

**Contribution of work** \
袁龙: E-R Diagram, Create Table(including Report)  \
于斯瑶: Data Import and Optimize,    \
赵钊: Basic operations compare with File I/O,       \
Together: DB Design

## Task 1: E-R Diagram
We use [Visio](https://visio.iruanhui.cn/?bd_vid=9042954634973670880) to draw the E-R diagram.
![E-R diagram](pic\\E-R diagram.png)

## Task 2: Database Design
### 1.E-R Diagram By DataGrip
![E-R diagram by DataGrip](pic\\database diagram.png)

### 2.Table Design Description
**toDo**: Finish the report mainly focus on \
(1) What tables have been designed and the reasons 
for this design (the reasons can be shorter) \
(2) What is the relationship between these tables \
(3) Why does this design conform to 3NF

## Task 3: Data Import


## Task 4: Compare DBMS with File I/O
### 1.Test Environment
**Hardware specification** \
CPU: 11th Gen Intel(R) Core(TM) i5-11300H @ 3.10GHz  \
RAM: 16.0 GB  \
**Software specification** \
DBMS: `postgresql-12.5`  \
OS: Windows 10 Home Chinese Version  \
Programing language: `Java 13.0.2`, `python 3.10.9`   \
IDE: `IntelliJ IDEA 2018.3.3` for Java, `Pycharm Community Edition 2021.3.1` for python \
Additional libraries: `postgresql-42.6.0.jar`   

### 2.Test Description
In this task, we mainly use the data in file `user.csv`, so we mainly focus on 
table `user` and `follow`.
In the basic part, we will test some DQL and some DML including insert, update, 
delete operations then compare them with `java.io` . 
In the advanced task, we will compare DBMS with indexes with Java implemented 
`B-tree`, `B+ tree`; using multi-user and multi-threading to test concurrency; 
comparing `postgresql` with `MYSQL`; comparing `JDBC` with `Python Database Connection`.

### 3.Script Description
There are mainly 4 java file we used in finishing the basic part.\
#### DataHandler.java
This is an interface which contains some methods that will be implemented
in class `FileHandler` and `DatabaseHandler`.The methods are listed below.
There are very detailed annotation in the source code that explained every
method.
```java
public interface DataHandler {
    public List<String> queryAllUsersName();
    public List<String> queryNameByMIDRange(long min, long max);
    public List<String> queryNameByLevel(int level);
    public List<String> queryNameByMID2Digits(char c1, char c2);
    public List<String> queryLongestName();
    public int queryDistinctBirthdayCnt();
    public List<String> queryMostFollowersUserName();
    public void insertUser(long mid, String name, String sex, 
                           String birth, int lv, String sign, 
                           List<Long> fol, String idt);
    public void updateSexByMID(long mid, String sex);
    public void updateFollowersByMID(long up_mid, long fans_mid);
    public void deleteUserByMID(long mid);
}
```
#### FileHandler.java
This is a script only use `java.io` and `java.util` to implements the method
in the interface `DataHandler`. Instead, we use a lot of String operations 
such as `split()`, `substring()` and `equals()`.
#### DatabaseHandler.java


#### Client.java


#### jdbc.properties



### 4.Basic Comparison


