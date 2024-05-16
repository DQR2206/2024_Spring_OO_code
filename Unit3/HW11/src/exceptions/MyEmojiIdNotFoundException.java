package exceptions;

import com.oocourse.spec3.exceptions.EmojiIdNotFoundException;

import java.util.HashMap;

public class MyEmojiIdNotFoundException extends EmojiIdNotFoundException {
    private static int count = 0;
    private static final HashMap<Integer, Integer> eachIdCnt = new HashMap<>();
    private final int id;

    public MyEmojiIdNotFoundException(int id) {
        this.id = id;
        count++;
        eachIdCnt.put(id, eachIdCnt.getOrDefault(id, 0) + 1);
    }

    public void print() {
        System.out.printf("einf-%d, %d-%d\n", count, id, eachIdCnt.get(id));
    }
}
