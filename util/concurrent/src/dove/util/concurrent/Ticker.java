package dove.util.concurrent;

public abstract class Ticker
        implements Runnable {
    private long tickTime;

    private boolean running = false;

    private Thread t = null;

    public Ticker(long tickTime) {
        this.tickTime = tickTime;
    }

    public void start() {
        if (running)
            throw new IllegalStateException("Ticker is already running");

        running = true;

        t = new Thread(this::run);
        t.setName("tickerthread");
        t.setDaemon(true);
        t.start();
    }

    public void stop() {
        running = false;

        t.interrupt();

        t = null;
    }

    protected abstract void nextTick();

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(tickTime);
            }
            catch (InterruptedException ignored) {
            }

            nextTick();
        }
    }

    public void enforceTick() {
        if (t == null)
            throw new IllegalStateException("Ticker hasn't been started");

        t.interrupt();
    }

    public long getTickTime() {
        return tickTime;
    }

    public void setFrequency(long frequency) {
        this.tickTime = frequency;
    }
}