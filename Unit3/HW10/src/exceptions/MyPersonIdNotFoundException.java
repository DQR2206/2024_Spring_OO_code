package exceptions;

import com.oocourse.spec2.exceptions.PersonIdNotFoundException;

import java.util.HashMap;

public class MyPersonIdNotFoundException extends PersonIdNotFoundException {
    private static int count = 0;
    private static final HashMap<Integer,Integer> eachIdCnt = new HashMap<>();
    private final int id;

    public MyPersonIdNotFoundException(int id) {
        this.id = id;
        count++;
        eachIdCnt.put(id,eachIdCnt.getOrDefault(id,0) + 1);
    }

    @Override
    public void print() {
        System.out.printf("pinf-%d, %d-%d\n",count,id,eachIdCnt.get(id));
    }
}
