package task4;

import java.util.Scanner;

public class ConcurrencyTest {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int opt = sc.nextInt();
        for (int i = 0; i < 5; i++) {
            DatabaseHandler dh = new DatabaseHandler();
            dh.openDB();
            dh.truncateUser();
            dh.closeDB();
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
}
