import com.oocourse.spec2.main.Network;
import com.oocourse.spec2.main.Person;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Collection;
import java.util.Random;
import java.util.Arrays;

@RunWith(Parameterized.class)
public class MyNetworkTest {
    private Network network;

    public MyNetworkTest(Network network) {
        this.network = network;
    }

    @Parameters
    public static Collection prepareData() {
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        int testNum = 1000;
        Object[][]  objects = new Object[testNum][];
        for (int i = 0; i < testNum; i++) { // 生成一个网络 里边得有人
            MyNetwork network = new MyNetwork();
            int personNum = 50;
            for (int j = 0; j < personNum; j++) {
                MyPerson person = new MyPerson(j, "name" + j,100);
                try {
                    network.addPerson(person);
                } catch (Exception e) {
                    continue;
                }
            }
            int relationNum = 50;
            for (int j = 0; j < relationNum; j++) {
                int id1 = random.nextInt(personNum);
                int id2 = random.nextInt(personNum);
                if (id1 != id2) {
                    try {
                        network.addRelation(id1, id2, random.nextInt(50));
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
            objects[i] = new Object[]{network};
        }
        return Arrays.asList(objects);
    }

    @Test
    public void queryCoupleSum() {
        /*@ ensures \result ==
        @         (\sum int i, j; 0 <= i && i < j && j < persons.length
        @                         && persons[i].acquaintance.length > 0 && queryBestAcquaintance(persons[i].getId()) == persons[j].getId()
        @                         && persons[j].acquaintance.length > 0 && queryBestAcquaintance(persons[j].getId()) == persons[i].getId();
        @                         1);
        @*/
        int count = 0;
        for (int i = 0;i < ((MyNetwork)network).getPersons().length;i++) {
            for (int j = i + 1;j < ((MyNetwork)network).getPersons().length;j++) {
                MyPerson person1 = (MyPerson) ((MyNetwork)network).getPersons()[i];
                MyPerson person2 = (MyPerson) ((MyNetwork)network).getPersons()[j];
                int id1 = person1.getId();
                int id2 = person2.getId();
                int best1 = -1;
                int best2 = -1;
                try {
                    best1 = ((MyNetwork)network).queryBestAcquaintance(id1);
                    best2 = ((MyNetwork)network).queryBestAcquaintance(id2);
                } catch (Exception e) {
                    continue;
                }
                if (best1 == person2.getId() && best2 == person1.getId()) {
                    count++;
                }
            }
        }
        Person[] oldPersons = ((MyNetwork)network).getPersons();
        int count1 = network.queryCoupleSum();
        Assert.assertEquals(count, count1);
        // 检查数组中元素有没有变化
        Person[] persons = ((MyNetwork)network).getPersons();
        Assert.assertEquals(oldPersons.length, persons.length);
        for (int i = 0; i < oldPersons.length; i++) {
            Assert.assertTrue(((MyPerson) oldPersons[i]).strictEquals((MyPerson) persons[i]));
        }
    }
}