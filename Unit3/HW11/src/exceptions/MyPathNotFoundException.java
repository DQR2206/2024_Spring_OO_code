package exceptions;

import com.oocourse.spec3.exceptions.PathNotFoundException;

import java.util.HashMap;

public class MyPathNotFoundException extends PathNotFoundException {
    private static int count = 0;
    private static HashMap<Integer,Integer> eachIdCnt = new HashMap<>(4097);
    private int id1;
    private int id2;

    public MyPathNotFoundException(int id1,int id2) {
        this.id1 = id1;
        this.id2 = id2;
        count++;
        eachIdCnt.put(id1,eachIdCnt.getOrDefault(id1,0) + 1);
        eachIdCnt.put(id2,eachIdCnt.getOrDefault(id2,0) + 1);
    }

    public void print() {
        int min = Math.min(id1,id2);
        int max = Math.max(id1,id2);
        System.out.printf("pnf-%d, %d-%d, %d-%d\n",
                count,min,eachIdCnt.get(min),max,eachIdCnt.get(max));
    }
}
