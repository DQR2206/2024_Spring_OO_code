import com.oocourse.spec3.main.Message;
import com.oocourse.spec3.main.Person;
import com.oocourse.spec3.main.RedEnvelopeMessage;
import com.oocourse.spec3.main.NoticeMessage;
import com.oocourse.spec3.main.Tag;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

public class MyPerson implements Person {
    private final int id;
    private final String name;
    private final int age;
    private int money = 0;
    private int socialValue = 0;
    private final HashMap<Integer,Person> acquaintance = new HashMap<>();
    private final HashMap<Integer,Integer> value = new HashMap<>();
    private final HashMap<Integer, Tag> tags = new HashMap<>();
    private final List<Message> messages = new LinkedList<>();
    private final TreeSet<Person> sortedAquaintance = new TreeSet<>(new Comparator<Person>() {
        @Override
        public int compare(Person o1, Person o2) { //按照value从高到低排序 id从小到大
            int r = value.get(o2.getId()).compareTo(value.get(o1.getId()));
            if (r == 0) {
                return Integer.compare(o1.getId(),o2.getId());
            } else {
                return r;
            }
        }
    });

    public MyPerson(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getAge() {
        return age;
    }

    @Override
    public boolean containsTag(int id) {
        return tags.containsKey(id);
    }

    @Override
    public Tag getTag(int id) {
        return tags.get(id);
    }

    @Override
    public void addTag(Tag tag) {
        tags.put(tag.getId(),tag);
    }

    @Override
    public void delTag(int id) {
        tags.remove(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Person) {
            return (((Person) obj).getId() == id);
        } else {
            return false;
        }
    }

    @Override
    public boolean isLinked(Person person) {
        return acquaintance.containsValue(person) || person.getId() == id;
    }

    @Override
    public int queryValue(Person person) {
        if (acquaintance.containsKey(person.getId())) {
            return value.get(person.getId());
        } else {
            return 0;
        }
    }

    @Override
    public void addSocialValue(int num) {
        this.socialValue += num;
    }

    @Override
    public int getSocialValue() {
        return this.socialValue;
    }

    @Override
    public List<Message> getMessages() {
        return new LinkedList<>(messages);
    }

    @Override
    public List<Message> getReceivedMessages() {
        if (this.messages.size() < 5) {
            return new LinkedList<>(messages);
        } else {
            return new LinkedList<>(messages.subList(0,5));
        }
    }

    @Override
    public void addMoney(int num) {
        this.money += num;
    }

    @Override
    public int getMoney() {
        return this.money;
    }

    public void addRelation(Person person,int value) {
        acquaintance.put(person.getId(),person);
        this.value.put(person.getId(),value);
        sortedAquaintance.add(person);
    }

    public void modifyRelation(Person person, int value) {
        sortedAquaintance.remove(person);
        this.value.put(person.getId(),this.value.get(person.getId()) + value);
        sortedAquaintance.add(person);
    }

    public void deleteRelation(Person person) {
        sortedAquaintance.remove(person);
        acquaintance.remove(person.getId());
        value.remove(person.getId());
        for (Tag tag : tags.values()) {
            if (tag.hasPerson(person)) {
                tag.delPerson(person);
            }
        }
    }

    public int getAcquaintanceSize() {
        return acquaintance.size();
    }

    public HashMap<Integer,Person> getAcquaintance() {
        return acquaintance;
    }

    public Person getBestAcquaintance() {
        return sortedAquaintance.first();
    }

    public void addMessage(Message message) { //加到0位置上
        this.socialValue += message.getSocialValue();
        if (message instanceof RedEnvelopeMessage) {
            this.money += ((RedEnvelopeMessage) message).getMoney();
        }
        this.messages.add(0,message);
    }

    public void clearNotices() {
        this.messages.removeIf(message -> message instanceof NoticeMessage);
    }
}
