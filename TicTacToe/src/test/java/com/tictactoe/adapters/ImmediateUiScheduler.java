package com.tictactoe.adapters;

public final class ImmediateUiScheduler implements UiScheduler {

    @Override
    public void runInBackground(Runnable task) {
        task.run();
    }

    @Override
    public void runOnUiThread(Runnable task) {
        task.run();
    }
}
