package exceptions;

import com.oocourse.spec2.exceptions.RelationNotFoundException;

import java.util.HashMap;

public class MyRelationNotFoundException extends RelationNotFoundException {
    private static int count = 0;
    private static final HashMap<Integer,Integer> eachIdCnt = new HashMap<>();
    private final int id1;
    private final int id2;

    public MyRelationNotFoundException(int id1, int id2) {
        this.id1 = id1;
        this.id2 = id2;
        eachIdCnt.put(id1,eachIdCnt.getOrDefault(id1,0) + 1);
        eachIdCnt.put(id2,eachIdCnt.getOrDefault(id2,0) + 1);
        count++;
    }

    @Override
    public void print() {
        int min = Math.min(id1, id2);
        int max = Math.max(id1,id2);
        System.out.printf("rnf-%d, %d-%d, %d-%d\n",
                count,min,eachIdCnt.get(min),max,eachIdCnt.get(max));
    }

}
