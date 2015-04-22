package dove.desktop.timer;

import java.util.ArrayList;
import java.util.Timer;

/**
 * Created by Babits on 20/04/2015.
 */
public class DesktopScheduler {
    private Timer timer;

    private ArrayList<String> ids;

    public DesktopScheduler() {
        ids = new ArrayList<>();

        timer = new Timer("Desktop Task Scheduler", true);
    }

    public void schedule(DesktopTask task, long first, long deltaTime) {
        if (ids.contains(task.getID()))
            throw new IllegalStateException("Task is already running");

        ids.add(task.getID());

        timer.schedule(task, first, deltaTime);
    }

    public void removeTask(DesktopTask task) {
        task.cancel();

        ids.remove(task.getID());
    }
}
