package game.testutil;

import framework.UiScheduler;
import java.util.ArrayList;
import java.util.List;

/**
 * Captures background and UI tasks so a test can run them manually, one at a
 * time, to simulate a pending AI move or a queued UI update.
 */
public final class CapturingUiScheduler implements UiScheduler {

    private final List<Runnable> backgroundTasks = new ArrayList<>();
    private final List<Runnable> uiTasks = new ArrayList<>();

    @Override
    public void runInBackground(Runnable task) {
        backgroundTasks.add(task);
    }

    @Override
    public void runOnUiThread(Runnable task) {
        uiTasks.add(task);
    }

    public void runNextBackgroundTask() {
        backgroundTasks.remove(0).run();
    }

    public void runNextUiTask() {
        uiTasks.remove(0).run();
    }

    public int pendingBackgroundTasks() {
        return backgroundTasks.size();
    }

    public int pendingUiTasks() {
        return uiTasks.size();
    }
}