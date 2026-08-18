package com.tictactoe.framework.ui;

import com.tictactoe.game.adapters.UiScheduler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.SwingUtilities;

public final class SwingUiScheduler implements UiScheduler {

    private final ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "ai-move");
        thread.setDaemon(true);
        return thread;
    });

    @Override
    public void runInBackground(Runnable task) {
        backgroundExecutor.execute(task);
    }

    @Override
    public void runOnUiThread(Runnable task) {
        SwingUtilities.invokeLater(task);
    }
}
