import com.oocourse.spec2.main.Person;
import com.oocourse.spec2.main.Tag;

import java.util.HashMap;

public class MyTag implements Tag {
    private final int id;
    private final HashMap<Integer, Person> persons = new HashMap<>();
    private int ageSum = 0;
    private int agePowSum = 0;

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
    }

    @Override
    public boolean hasPerson(Person person) {
        return persons.containsKey(person.getId());
    }

    @Override
    public int getValueSum() {
        int sum = 0;
        for (Person person : persons.values()) {
            for (Person other : persons.values()) {
                if (person.isLinked(other)) {
                    sum += person.queryValue(other);
                }
            }
        }
        return sum;
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
    }

    @Override
    public int getSize() {
        return persons.size();
    }
}
