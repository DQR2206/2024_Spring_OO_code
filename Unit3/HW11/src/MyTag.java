import com.oocourse.spec3.main.Message;
import com.oocourse.spec3.main.Person;
import com.oocourse.spec3.main.RedEnvelopeMessage;
import com.oocourse.spec3.main.Tag;

import java.util.HashMap;

public class MyTag implements Tag {
    private final int id;
    private final HashMap<Integer, Person> persons = new HashMap<>();
    private int ageSum = 0;
    private int agePowSum = 0;
    private int valueSum = 0;

    public MyTag(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tag) {
            return ((Tag) obj).getId() == id;
        } else {
            return false;
        }
    }

    @Override
    public void addPerson(Person person) {
        ageSum += person.getAge();
        agePowSum += person.getAge() * person.getAge();
        persons.put(person.getId(), person);
        for (int id : persons.keySet()) {
            if (person.isLinked(persons.get(id))) {
                valueSum += 2 * person.queryValue(persons.get(id));
            }
        }
    }

    @Override
    public boolean hasPerson(Person person) {
        return persons.containsKey(person.getId());
    }

    @Override
    public int getValueSum() {
        return this.valueSum;
    }

    @Override
    public int getAgeMean() {
        if (persons.isEmpty()) {
            return 0;
        } else {
            return ageSum / persons.size();
        }
    }

    @Override
    public int getAgeVar() {
        if (persons.isEmpty()) {
            return 0;
        } else {
            return (agePowSum - 2 * ageSum * getAgeMean() +
                    persons.size() * getAgeMean() * getAgeMean()) / persons.size();
        }
    }

    @Override
    public void delPerson(Person person) {
        ageSum -= person.getAge();
        agePowSum -= person.getAge() * person.getAge();
        persons.remove(person.getId());
        for (int id : persons.keySet()) {
            if (person.isLinked(persons.get(id))) {
                valueSum -= 2 * person.queryValue(persons.get(id));
            }
        }
    }

    @Override
    public int getSize() {
        return persons.size();
    }

    public void addValueSum(int value) {
        valueSum += 2 * value;
    }

    public void addMessage(Message message) {
        for (Person person : persons.values()) {
            person.addSocialValue(message.getSocialValue());
        }
        if (message instanceof RedEnvelopeMessage) {
            if (!persons.isEmpty()) {
                int i = ((RedEnvelopeMessage) message).getMoney() / persons.size();
                for (Person person : persons.values()) {
                    person.addMoney(i);
                }
            }
        }
    }
}
