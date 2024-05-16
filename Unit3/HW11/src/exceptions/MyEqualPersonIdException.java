package exceptions;

import com.oocourse.spec3.exceptions.EqualPersonIdException;

import java.util.HashMap;

public class MyEqualPersonIdException extends EqualPersonIdException {
    private static int count = 0;
    private static final HashMap<Integer,Integer> eachIdCnt = new HashMap<>();
    private final int id;

    public MyEqualPersonIdException(int id) {
        this.id = id;
        count++;
        eachIdCnt.put(id,eachIdCnt.getOrDefault(id,0) + 1);
    }

    @Override
    public void print() {
        System.out.printf("epi-%d, %d-%d\n",count,id,eachIdCnt.get(id));
    }

}
