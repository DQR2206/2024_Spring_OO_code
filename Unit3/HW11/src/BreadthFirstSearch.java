import com.oocourse.spec3.main.Person;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class BreadthFirstSearch {

    public static int shortestPath(MyPerson person1, MyPerson person2) {
        if (person1.getId() == person2.getId()) {
            return 0;
        }
        HashMap<Integer, Integer> visited = new HashMap<>();
        visited.put(person1.getId(),0);
        Queue<MyPerson> queue = new LinkedList<>();
        queue.add(person1);
        while (!queue.isEmpty()) {
            MyPerson now = queue.poll();
            int step = visited.get(now.getId());
            for (Person next : now.getAcquaintance().values()) {
                if (visited.containsKey(next.getId())) {
                    continue;
                }
                if (next.getId() == person2.getId()) {
                    return step + 1;
                }
                queue.add((MyPerson) next);
                visited.put(next.getId(),step + 1);
            }
        }
        return -1;
    }

    public static boolean isConnected(MyPerson person1,MyPerson person2) {
        if (person1.getId() == person2.getId()) {
            return true;
        }
        HashMap<Integer, Boolean> visited1 = new HashMap<>();
        HashMap<Integer, Boolean> visited2 = new HashMap<>();
        visited1.put(person1.getId(),true);
        visited2.put(person2.getId(),true);
        Queue<MyPerson> queue1 = new LinkedList<>();
        Queue<MyPerson> queue2 = new LinkedList<>();
        queue1.add(person1);
        queue2.add(person2);
        while (!queue1.isEmpty() && !queue2.isEmpty()) {
            if (queue1.size() < queue2.size()) {
                MyPerson now = queue1.poll();
                if (next(now,visited1,visited2,queue1)) {
                    return true;
                }
            } else {
                MyPerson now = queue2.poll();
                if (next(now,visited2,visited1,queue2)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean next(MyPerson now,HashMap<Integer,Boolean> visited,
                                HashMap<Integer,Boolean> visited2,Queue<MyPerson> queue) {
        for (Person next : now.getAcquaintance().values()) {
            if (visited.containsKey(next.getId())) {
                continue;
            }
            if (visited2.containsKey(next.getId())) {
                return true;
            }
            queue.add((MyPerson) next);
            visited.put(next.getId(),true);
        }
        return false;
    }
}
