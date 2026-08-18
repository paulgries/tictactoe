package game.testutil;

import framework.UiScheduler;

/**
 * Runs every task synchronously on the calling thread; useful for wiring
 * presenters/controllers in tests where scheduling is not under test.
 */
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