package exceptions;

import com.oocourse.spec3.exceptions.AcquaintanceNotFoundException;

import java.util.HashMap;

public class MyAcquaintanceNotFoundException extends AcquaintanceNotFoundException {
    private static int count = 0;
    private static HashMap<Integer,Integer> eachIdCnt = new HashMap<>(4097);
    private int id;

    public MyAcquaintanceNotFoundException(int id) {
        this.id = id;
        count++;
        eachIdCnt.put(id,eachIdCnt.getOrDefault(id,0) + 1);
    }

    public void print() {
        System.out.printf("anf-%d, %d-%d\n",count,id,eachIdCnt.get(id));
    }

}
