package exceptions;

import com.oocourse.spec2.exceptions.TagIdNotFoundException;

import java.util.HashMap;

public class MyTagIdNotFoundException extends TagIdNotFoundException {
    private static int count = 0;
    private static HashMap<Integer, Integer> eachIdCnt = new HashMap<>(4097);
    private int id;

    public MyTagIdNotFoundException(int id) {
        this.id = id;
        count++;
        eachIdCnt.put(id, eachIdCnt.getOrDefault(id, 0) + 1);
    }

    public void print() {
        System.out.printf("tinf-%d, %d-%d\n",count,id,eachIdCnt.get(id));
    }
}
