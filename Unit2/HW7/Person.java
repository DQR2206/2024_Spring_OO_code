public class Person {
    private final Integer id;
    private final Integer fromFloor;
    private final Integer toFloor;

    public Person(Integer id, Integer fromFloor, Integer toFloor) {
        this.id = id;
        this.fromFloor = fromFloor;
        this.toFloor = toFloor;
    }

    public Integer getFromFloor() {
        return this.fromFloor;
    }

    public Integer getToFloor() {
        return this.toFloor;
    }

    public Integer getId() {
        return this.id;
    }

}
