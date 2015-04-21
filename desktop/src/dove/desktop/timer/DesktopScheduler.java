package dove.desktop.timer;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Babits on 20/04/2015.
 */
public class DesktopScheduler {
    private Timer timer;

    public DesktopScheduler() {
        timer = new Timer("Desktop Task Scheduler", true);
    }

    public void scheduler(TimerTask task, long first, long deltaTime) {
        timer.schedule(task, first, deltaTime);
    }

    public void removeTask(TimerTask task) {
        task.cancel();
    }
}
