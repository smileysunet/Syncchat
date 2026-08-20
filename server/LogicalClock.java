package server;

public class LogicalClock {

    private long offset;

    public LogicalClock(long offset) {
        this.offset = offset;
    }

    public long getTime() {
        return System.currentTimeMillis() + offset;
    }

    public void adjust(long adjustment) {
        offset += adjustment;
    }
}