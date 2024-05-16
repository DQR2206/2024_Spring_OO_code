package exceptions;

import com.oocourse.spec3.exceptions.EqualMessageIdException;

import java.util.HashMap;

public class MyEqualMessageIdException extends EqualMessageIdException {
    private static int count = 0;
    private static HashMap<Integer, Integer> eachIdCnt = new HashMap<>();
    private final int id;

    public MyEqualMessageIdException(int id) {
        this.id = id;
        count++;
        eachIdCnt.put(id, eachIdCnt.getOrDefault(id, 0) + 1);
    }

    public void print() {
        System.out.printf("emi-%d, %d-%d\n", count, id, eachIdCnt.get(id));
    }
}
