import com.oocourse.spec3.main.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.HashMap;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class MyNetworkTest {
    private MyNetwork myNetwork;
    private MyNetwork cloneNetwork;

    public MyNetworkTest(MyNetwork myNetwork, MyNetwork cloneNetwork) {
        this.myNetwork = myNetwork;
        this.cloneNetwork = cloneNetwork;
    }

    @Parameters
    public static Collection prepareData() {
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        int testNum = 1000;
        Object[][] object = new Object[testNum][];
        for (int i = 0;i < testNum; i++) {
            MyNetwork myNetwork = new MyNetwork();
            MyNetwork cloneNetwork = new MyNetwork();
            MyPerson person1 = new MyPerson(1, "dqr", 20);
            MyPerson person2 = new MyPerson(2, "tpx", 20);
            try {
                myNetwork.addPerson(person1);
                myNetwork.addPerson(person2);
                cloneNetwork.addPerson(person1);
                cloneNetwork.addPerson(person2);
            } catch (Exception ignored) {
                ;
            }
            try {
                myNetwork.addRelation(1, 2, 100);
                cloneNetwork.addRelation(1, 2, 200);
            } catch (Exception ignored) {
                ;
            }
            int num = random.nextInt(100) + 200;
            int cnt = 0;
            for (int j = 0; j < num;j ++) {
                int type = random.nextInt(9);
                if (type < 3) {
                    int emojiNum = random.nextInt(10) + 5;
                    while (emojiNum > 0) {
                        MyEmojiMessage emojiMessage = new MyEmojiMessage(cnt++, random.nextInt(50), person1, person2);
                        try {
                            myNetwork.storeEmojiId(emojiMessage.getId());
                            cloneNetwork.storeEmojiId(emojiMessage.getId());
                            myNetwork.addMessage(emojiMessage);
                            cloneNetwork.addMessage(emojiMessage);
                        } catch (Exception ignored) {
                            ;
                        }
                        emojiNum--;
                    }
                } else if (type < 6) {
                    MyRedEnvelopeMessage redEnvelopeMessage = new MyRedEnvelopeMessage(cnt++, random.nextInt(40), person1, person2);
                    try {
                        myNetwork.addMessage(redEnvelopeMessage);
                        cloneNetwork.addMessage(redEnvelopeMessage);
                    } catch (Exception ignored) {
                        ;
                    }
                } else {
                    MyNoticeMessage noticeMessage = new MyNoticeMessage(cnt++, String.valueOf(random.nextInt(40)), person1, person2);
                    try {
                        myNetwork.addMessage(noticeMessage);
                        cloneNetwork.addMessage(noticeMessage);
                    } catch (Exception ignored) {
                        ;
                    }
                }
            }
            cnt = (int) (cnt * 0.7); // 70% of the messages will be sent
            while (cnt > 0) {
                int id = random.nextInt(cnt);
                try {
                    myNetwork.sendMessage(id);
                    cloneNetwork.sendMessage(id);
                } catch (Exception ignored) {
                    ;
                }
                cnt--;
            }
            object[i] = new Object[]{myNetwork, cloneNetwork};
        }
        return Arrays.asList(object);
    }


    @Test
    public void deleteColdEmojiTest() {
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        int limit = random.nextInt(10) + 1;
        int [] oldEmojiIdList = myNetwork.getEmojiIdList();
        int [] oldEmojiHeatList = myNetwork.getEmojiHeatList();
        HashMap<Integer, Integer> map = new HashMap<>();
        for (int i = 0;i < oldEmojiIdList.length; i++) {
            map.put(oldEmojiIdList[i], oldEmojiHeatList[i]);
        }
        Message [] messageList1 = myNetwork.getMessages();
        int size = messageList1.length;
        int emojiCnt1 = 0;
        for (Message message : messageList1) {
            if (message != null) {
                if (message instanceof MyEmojiMessage) {
                    emojiCnt1++;
                }
            }
        }
        int ans = myNetwork.deleteColdEmoji(limit);
        Message [] newMessageList = myNetwork.getMessages();

        int emojiCnt2 = 0;
        for (Message message : newMessageList) {
            if (message != null) {
                if (message instanceof MyEmojiMessage) {
                    emojiCnt2++;
                }
            }
        }
        assertEquals(size - emojiCnt1,newMessageList.length - emojiCnt2);
        int expect = 0;
        for (int i = 0;i < oldEmojiHeatList.length; i++) {
            if (oldEmojiHeatList[i] < limit) {
                assertFalse(myNetwork.containsEmojiId(oldEmojiIdList[i]));
            } else {
                expect++;
            }
        }
        assertEquals(expect, ans);
        for (Message message : newMessageList) {
            if (message != null) {
                if (message instanceof MyEmojiMessage) {
                    MyEmojiMessage emojiMessage = (MyEmojiMessage) message;
                    if (map.containsKey(emojiMessage.getEmojiId()) && map.get(emojiMessage.getEmojiId()) < limit) {
                        assertEquals(1, 0);
                    }
                }
            }
        }
    }
}