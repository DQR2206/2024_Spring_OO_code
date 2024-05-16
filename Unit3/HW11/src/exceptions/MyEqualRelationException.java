package exceptions;

import com.oocourse.spec3.exceptions.EqualRelationException;

import java.util.HashMap;

public class MyEqualRelationException extends EqualRelationException {
    private static int count = 0;
    private static final HashMap<Integer,Integer> eachIdCnt = new HashMap<>();
    private final int id1;
    private final int id2;

    public MyEqualRelationException(int id1, int id2) {
        this.id1 = id1;
        this.id2 = id2;
        count++;
        eachIdCnt.put(id1,eachIdCnt.getOrDefault(id1,0) + 1);
        if (id1 != id2) {
            eachIdCnt.put(id2,eachIdCnt.getOrDefault(id2,0) + 1);
        }
    }

    @Override
    public void print() {
        int min = Math.min(id1,id2);
        int max = Math.max(id1,id2);
        System.out.printf("er-%d, %d-%d, %d-%d\n",
                count,min,eachIdCnt.get(min),max,eachIdCnt.get(max));
    }

}
