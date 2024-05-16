import com.oocourse.spec3.main.Network;
import com.oocourse.spec3.main.Person;
import com.oocourse.spec3.main.Message;
import com.oocourse.spec3.main.Tag;
import com.oocourse.spec3.main.RedEnvelopeMessage;
import com.oocourse.spec3.main.EmojiMessage;
import exceptions.MyEmojiIdNotFoundException;
import exceptions.MyEqualMessageIdException;
import exceptions.MyEqualPersonIdException;
import exceptions.MyEqualRelationException;
import exceptions.MyEqualTagIdException;
import exceptions.MyEqualEmojiIdException;
import exceptions.MyMessageIdNotFoundException;
import exceptions.MyPersonIdNotFoundException;
import exceptions.MyRelationNotFoundException;
import exceptions.MyTagIdNotFoundException;
import exceptions.MyAcquaintanceNotFoundException;
import exceptions.MyPathNotFoundException;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

public class MyNetwork implements Network {
    private final HashMap<Integer, Person> persons = new HashMap<>();
    private final HashMap<Integer, Message> messages = new HashMap<>();
    private final HashMap<Integer, Integer> emojiHeatList = new HashMap<>();
    private final ArrayList<MyTag> tags = new ArrayList<>();
    private int block;
    private int triple;

    public MyNetwork() {
        this.block = 0;
        this.triple = 0;
    }

    @Override
    public boolean containsPerson(int id) {
        return persons.containsKey(id);
    }

    @Override
    public Person getPerson(int id) {
        return persons.get(id);
    }

    @Override
    public void addPerson(Person person) throws MyEqualPersonIdException {
        if (containsPerson(person.getId())) {
            throw new MyEqualPersonIdException(person.getId());
        } else {
            block++;
            persons.put(person.getId(), person);
        }
    }

    private void addTagValueSum(MyPerson person1, MyPerson person2, int value) {
        for (MyTag tag : this.tags) {
            if (tag.hasPerson(person1) && tag.hasPerson(person2)) {
                tag.addValueSum(value);
            }
        }
    }

    @Override
    public void addRelation(int id1, int id2, int value) throws
            MyPersonIdNotFoundException, MyEqualRelationException {
        if (!containsPerson(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (!containsPerson(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        } else if (getPerson(id1).isLinked(getPerson(id2))) {
            throw new MyEqualRelationException(id1,id2);
        } else {
            MyPerson person1 = (MyPerson) getPerson(id1);
            MyPerson person2 = (MyPerson) getPerson(id2);
            if (!BreadthFirstSearch.isConnected(person1,person2)) {
                this.block--;
            }
            if (person1.getAcquaintanceSize() < person2.getAcquaintanceSize()) {
                addTripleRelation(person1,person2);
            } else {
                addTripleRelation(person2,person1);
            }
            person1.addRelation(person2,value);
            person2.addRelation(person1,value);
            addTagValueSum(person1,person2,value);
        }
    }

    @Override
    public void modifyRelation(int id1, int id2, int value) throws
            MyPersonIdNotFoundException, MyEqualPersonIdException, MyRelationNotFoundException {
        if (!containsPerson(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (!containsPerson(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        } else if (id1 == id2) {
            throw new MyEqualPersonIdException(id1);
        } else if (!getPerson(id1).isLinked(getPerson(id2))) {
            throw new MyRelationNotFoundException(id1,id2);
        } else {
            MyPerson person1 = (MyPerson) getPerson(id1);
            MyPerson person2 = (MyPerson) getPerson(id2);
            int oldValue = person1.queryValue(person2);
            int newValue = (oldValue + value < 0) ? -oldValue : value;
            if (person1.queryValue(person2) + value <= 0) { // 失去联系 考虑block和triple的变化
                person1.deleteRelation(person2);
                person2.deleteRelation(person1);
                if (!BreadthFirstSearch.isConnected(person1,person2)) {
                    this.block++;
                }
                if (person1.getAcquaintanceSize() < person2.getAcquaintanceSize()) {
                    deleteTripleRelation(person1,person2);
                } else {
                    deleteTripleRelation(person2,person1);
                }
                addTagValueSum(person1,person2,newValue);
            } else {
                person1.modifyRelation(person2, value);
                person2.modifyRelation(person1, value);
                addTagValueSum(person1,person2,newValue);
            }
        }
    }

    @Override
    public int queryValue(int id1, int id2) throws
            MyPersonIdNotFoundException, MyRelationNotFoundException {
        if (!containsPerson(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (!containsPerson(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        } else if (!getPerson(id1).isLinked(getPerson(id2))) {
            throw new MyRelationNotFoundException(id1,id2);
        } else {
            return getPerson(id1).queryValue(getPerson(id2));
        }
    }

    @Override
    public boolean isCircle(int id1,int id2) throws MyPersonIdNotFoundException {
        if (!containsPerson(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (!containsPerson(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        } else { // 相当于在图中找这两人是否有一条路径 is-connected
            return BreadthFirstSearch.isConnected((MyPerson) getPerson(id1),
                    (MyPerson) getPerson(id2));
        }
    }

    @Override
    public int queryBlockSum() { // 两个人不联通
        return this.block;
    }

    @Override
    public int queryTripleSum() { // 三个人互相联通 三角形
        return this.triple;
    }

    @Override
    public void addTag(int personId, Tag tag)
            throws MyPersonIdNotFoundException, MyEqualTagIdException {
        if (!containsPerson(personId)) {
            throw new MyPersonIdNotFoundException(personId);
        } else if (getPerson(personId).containsTag(tag.getId())) {
            throw new MyEqualTagIdException(tag.getId());
        } else {
            getPerson(personId).addTag(tag);
            tags.add((MyTag) tag);
        }
    }

    @Override
    public void addPersonToTag(int personId1, int personId2, int tagId)
            throws MyPersonIdNotFoundException, MyEqualPersonIdException,
            MyRelationNotFoundException, MyTagIdNotFoundException {
        if (!containsPerson(personId1)) {
            throw new MyPersonIdNotFoundException(personId1);
        } else if (!containsPerson(personId2)) {
            throw new MyPersonIdNotFoundException(personId2);
        } else if (personId1 == personId2) {
            throw new MyEqualPersonIdException(personId1);
        } else if (!getPerson(personId2).isLinked(getPerson(personId1))) {
            throw new MyRelationNotFoundException(personId1,personId2);
        } else if (!getPerson(personId2).containsTag(tagId)) {
            throw new MyTagIdNotFoundException(tagId);
        } else if (getPerson(personId2).getTag(tagId).hasPerson(getPerson(personId1))) {
            throw new MyEqualPersonIdException(personId1);
        } else {
            if (getPerson(personId2).getTag(tagId).getSize() <= 1111) {
                getPerson(personId2).getTag(tagId).addPerson(getPerson(personId1));
            }
        }
    }

    @Override
    public int queryTagValueSum(int personId, int tagId)
            throws MyPersonIdNotFoundException, MyTagIdNotFoundException {
        if (!containsPerson(personId)) {
            throw new MyPersonIdNotFoundException(personId);
        } else if (!getPerson(personId).containsTag(tagId)) {
            throw new MyTagIdNotFoundException(tagId);
        } else {
            return getPerson(personId).getTag(tagId).getValueSum();
        }
    }

    @Override
    public int queryTagAgeVar(int personId, int tagId)
            throws MyPersonIdNotFoundException, MyTagIdNotFoundException {
        if (!containsPerson(personId)) {
            throw new MyPersonIdNotFoundException(personId);
        } else if (!getPerson(personId).containsTag(tagId)) {
            throw new MyTagIdNotFoundException(tagId);
        } else {
            return getPerson(personId).getTag(tagId).getAgeVar();
        }
    }

    @Override
    public void delPersonFromTag(int personId1, int personId2, int tagId)
            throws MyPersonIdNotFoundException, MyTagIdNotFoundException {
        if (!containsPerson(personId1)) {
            throw new MyPersonIdNotFoundException(personId1);
        } else if (!containsPerson(personId2)) {
            throw new MyPersonIdNotFoundException(personId2);
        } else if (!getPerson(personId2).containsTag(tagId)) {
            throw new MyTagIdNotFoundException(tagId);
        } else if (!getPerson(personId2).getTag(tagId).hasPerson(getPerson(personId1))) {
            throw new MyPersonIdNotFoundException(personId1);
        } else {
            getPerson(personId2).getTag(tagId).delPerson(getPerson(personId1));
        }
    }

    @Override
    public void delTag(int personId,int tagId)
            throws MyPersonIdNotFoundException, MyTagIdNotFoundException {
        if (!containsPerson(personId)) {
            throw new MyPersonIdNotFoundException(personId);
        } else if (!getPerson(personId).containsTag(tagId)) {
            throw new MyTagIdNotFoundException(tagId);
        } else {
            getPerson(personId).delTag(tagId);
        }
    }

    @Override
    public boolean containsMessage(int id) {
        return messages.containsKey(id);
    }

    @Override
    public void addMessage(Message message) throws
            MyEqualMessageIdException, MyEmojiIdNotFoundException, MyEqualPersonIdException {
        if (containsMessage(message.getId())) {
            throw new MyEqualMessageIdException(message.getId());
        } else if ((message instanceof EmojiMessage)
                && !containsEmojiId(((EmojiMessage) message).getEmojiId())) {
            throw new MyEmojiIdNotFoundException(((EmojiMessage) message).getEmojiId());
        } else if (message.getType() == 0 && message.getPerson1().equals(message.getPerson2())) {
            throw new MyEqualPersonIdException(message.getPerson1().getId());
        } else {
            messages.put(message.getId(), message);
        }
    }

    @Override
    public Message getMessage(int id) {
        return messages.get(id);
    }

    @Override
    public void sendMessage(int id) throws
            MyMessageIdNotFoundException, MyRelationNotFoundException, MyTagIdNotFoundException {
        if (!containsMessage(id)) {
            throw new MyMessageIdNotFoundException(id);
        } else if (getMessage(id).getType() == 0
                && !getMessage(id).getPerson1().isLinked(getMessage(id).getPerson2())) {
            throw new MyRelationNotFoundException(getMessage(id).getPerson1().getId(),
                    getMessage(id).getPerson2().getId());
        } else if (getMessage(id).getType() == 1
                && !getMessage(id).getPerson1().containsTag(getMessage(id).getTag().getId())) {
            throw new MyTagIdNotFoundException(getMessage(id).getTag().getId());
        } else {
            Message message = getMessage(id);
            if (message.getType() == 0) {
                message.getPerson1().addSocialValue(message.getSocialValue());
                if (message instanceof RedEnvelopeMessage) {
                    ((RedEnvelopeMessage) message).
                            getPerson1().addMoney(-((RedEnvelopeMessage) message).getMoney());
                }
                ((MyPerson)message.getPerson2()).addMessage(message);
            } else if (message.getType() == 1) {
                message.getPerson1().addSocialValue(message.getSocialValue());
                if (message instanceof RedEnvelopeMessage) {
                    if (((MyTag)message.getTag()).getSize() > 0) {
                        int i = ((RedEnvelopeMessage) message).getMoney() /
                                ((MyTag) message.getTag()).getSize();
                        message.getPerson1().addMoney(-i * ((MyTag) message.getTag()).getSize());
                    }
                }
                ((MyTag)message.getTag()).addMessage(message);
            }
            if (message instanceof EmojiMessage) {
                emojiHeatList.put(((EmojiMessage) message).getEmojiId(),
                        emojiHeatList.getOrDefault(((EmojiMessage) message).getEmojiId(), 0) + 1);
            }
            messages.remove(id);
        }

    }

    @Override
    public int querySocialValue(int id) throws MyPersonIdNotFoundException {
        if (!containsPerson(id)) {
            throw new MyPersonIdNotFoundException(id);
        } else {
            return ((MyPerson)getPerson(id)).getSocialValue();
        }
    }

    @Override
    public List<Message> queryReceivedMessages(int id) throws MyPersonIdNotFoundException {
        if (!containsPerson(id)) {
            throw new MyPersonIdNotFoundException(id);
        } else {
            return ((MyPerson)getPerson(id)).getReceivedMessages();
        }
    }

    @Override
    public boolean containsEmojiId(int id) {
        return emojiHeatList.containsKey(id);
    }

    @Override
    public void storeEmojiId(int id) throws MyEqualEmojiIdException {
        if (containsEmojiId(id)) {
            throw new MyEqualEmojiIdException(id);
        } else {
            emojiHeatList.put(id,0);
        }
    }

    @Override
    public int queryMoney(int id) throws MyPersonIdNotFoundException {
        if (!containsPerson(id)) {
            throw new MyPersonIdNotFoundException(id);
        } else {
            return ((MyPerson)getPerson(id)).getMoney();
        }
    }

    @Override
    public int queryPopularity(int id) throws MyEmojiIdNotFoundException {
        if (!containsEmojiId(id)) {
            throw new MyEmojiIdNotFoundException(id);
        } else {
            return emojiHeatList.get(id);
        }
    }

    @Override
    public int deleteColdEmoji(int limit) {
        emojiHeatList.entrySet().removeIf(entry -> entry.getValue() < limit);
        Iterator<Map.Entry<Integer, Message>> iterator1 = messages.entrySet().iterator();
        while (iterator1.hasNext()) {
            Message message = iterator1.next().getValue();
            if (message instanceof EmojiMessage &&
                    !containsEmojiId(((EmojiMessage) message).getEmojiId())) {
                iterator1.remove();
            }
        }
        return emojiHeatList.size();
    }

    @Override
    public void clearNotices(int personId) throws MyPersonIdNotFoundException {
        if (!containsPerson(personId)) {
            throw new MyPersonIdNotFoundException(personId);
        } else {
            ((MyPerson)getPerson(personId)).clearNotices();
        }
    }

    @Override
    public int queryBestAcquaintance(int personId)
            throws MyPersonIdNotFoundException, MyAcquaintanceNotFoundException {
        if (!containsPerson(personId)) {
            throw new MyPersonIdNotFoundException(personId);
        } else if (((MyPerson)getPerson(personId)).getAcquaintanceSize() == 0) {
            throw new MyAcquaintanceNotFoundException(personId);
        } else {
            return ((MyPerson)getPerson(personId)).getBestAcquaintance().getId();
        }
    }

    @Override
    public int queryCoupleSum() {
        int sum = 0;
        for (Person person : persons.values()) {
            if (((MyPerson)person).getAcquaintanceSize() > 0) {
                int bestPersonId = ((MyPerson) person).getBestAcquaintance().getId();
                MyPerson bestPerson = (MyPerson) getPerson(bestPersonId);
                if (bestPerson.getBestAcquaintance().getId() == person.getId()) {
                    sum++;
                }
            }
        }
        return sum / 2;
    }

    @Override
    public int queryShortestPath(int personId1, int personId2)
            throws MyPersonIdNotFoundException, MyPathNotFoundException {
        if (!containsPerson(personId1)) {
            throw new MyPersonIdNotFoundException(personId1);
        } else if (!containsPerson(personId2)) {
            throw new MyPersonIdNotFoundException(personId2);
        } else if (!isCircle(personId1,personId2)) {
            throw new MyPathNotFoundException(personId1,personId2);
        } else {
            int result = BreadthFirstSearch.shortestPath((MyPerson) getPerson(personId1),
                    (MyPerson) getPerson(personId2));
            return result > 1 ? result - 1 : 0;
        }
    }

    private void addTripleRelation(MyPerson person1,MyPerson person2) { //这里选择数量比较少的一方算是优化
        for (Person person : person1.getAcquaintance().values()) {
            if (person2.isLinked(person)) {
                this.triple++;
            }
        }
    }

    private void deleteTripleRelation(MyPerson person1,MyPerson person2) {
        for (Person person : person1.getAcquaintance().values()) {
            if (person2.isLinked(person)) {
                this.triple--;
            }
        }
    }

    public Message[] getMessages() {
        Message[] result = new Message[messages.size()];
        int i = 0;
        for (Message message : messages.values()) {
            result[i++] = message;
        }
        return result;
    }

    // 注意保持hashmap的顺序
    public int[] getEmojiIdList() {
        int[] result = new int[emojiHeatList.size()];
        int i = 0;
        for (int id : emojiHeatList.keySet()) {
            result[i++] = id;
        }
        return result;
    }

    public int[] getEmojiHeatList() {
        int[] result = new int[emojiHeatList.size()];
        int i = 0;
        for (int heat : emojiHeatList.values()) {
            result[i++] = heat;
        }
        return result;
    }

}
