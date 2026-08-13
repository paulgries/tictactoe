package com.tictactoe.adapters;

import java.util.ArrayDeque;
import java.util.Deque;

public final class CapturingUiScheduler implements UiScheduler {

    private final Deque<Runnable> backgroundTasks = new ArrayDeque<>();
    private final Deque<Runnable> uiTasks = new ArrayDeque<>();

    @Override
    public void runInBackground(Runnable task) {
        backgroundTasks.add(task);
    }

    @Override
    public void runOnUiThread(Runnable task) {
        uiTasks.add(task);
    }

    public void runNextBackgroundTask() {
        backgroundTasks.remove().run();
    }

    public void runNextUiTask() {
        uiTasks.remove().run();
    }
}
