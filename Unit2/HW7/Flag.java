public class Flag {
    enum State { BUSY, IDLE }

    private State state;

    public Flag() {
        this.state = State.IDLE;
    }

    public synchronized void setOccupied() {
        waitRelease(); // 两个轿厢共享一个flag 相当于对这=换乘楼层的访问进行了上锁 一个走另一个才能访问
        this.state = State.BUSY;
        notifyAll();
    }

    private synchronized void waitRelease() {
        notifyAll();
        while (this.state == State.BUSY) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void setRelease() {
        this.state = State.IDLE;
        notifyAll();
    }
}
