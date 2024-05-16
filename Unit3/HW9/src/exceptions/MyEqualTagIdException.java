package exceptions;

import com.oocourse.spec2.exceptions.EqualTagIdException;

import java.util.HashMap;

public class MyEqualTagIdException extends EqualTagIdException {
    private static int count = 0;
    private static HashMap<Integer,Integer> eachIdCnt = new HashMap<>(4097);
    private int id;

    public MyEqualTagIdException(int id) {
        this.id = id;
        count++;
        eachIdCnt.put(id,eachIdCnt.getOrDefault(id,0) + 1);
    }

    public void print() {
        System.out.printf("eti-%d, %d-%d\n",count,id,eachIdCnt.get(id));
    }
}
