package dove.util.concurrent;

public abstract class Progressable
        implements Runnable {
    private double progress;

    public synchronized double getProgress() {
        return progress;
    }

    public synchronized void setProgress(double progress) {
        this.progress = progress;
    }
}