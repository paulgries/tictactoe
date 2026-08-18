package com.tictactoe.game.adapters;

/**
 * Lets the controller run long computations (e.g. an AI move search) without blocking
 * the delivery mechanism's UI thread, while keeping this layer free of any UI framework
 * import. Implementations decide what "background" and "UI thread" mean.
 */
public interface UiScheduler {

    void runInBackground(Runnable task);

    void runOnUiThread(Runnable task);
}
