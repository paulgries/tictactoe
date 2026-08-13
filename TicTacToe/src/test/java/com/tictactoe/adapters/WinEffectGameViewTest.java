package com.tictactoe.adapters;

import static org.assertj.core.api.Assertions.assertThat;

import com.tictactoe.adapters.viewmodel.GameOutcomeKind;
import com.tictactoe.adapters.viewmodel.StatusViewModel;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class WinEffectGameViewTest {

    @Test
    void displayStatusWithWinTriggersTheEffectExactlyOnce() {
        FakeGameView delegate = new FakeGameView();
        AtomicInteger triggerCount = new AtomicInteger();
        GameView decorator = new WinEffectGameView(delegate, triggerCount::incrementAndGet);

        decorator.displayStatus(new StatusViewModel("X wins!", GameOutcomeKind.WIN));

        assertThat(triggerCount.get()).isEqualTo(1);
    }

    @Test
    void displayStatusWithWinStillDelegatesToTheWrappedView() {
        FakeGameView delegate = new FakeGameView();
        GameView decorator = new WinEffectGameView(delegate, () -> { });
        StatusViewModel status = new StatusViewModel("X wins!", GameOutcomeKind.WIN);

        decorator.displayStatus(status);

        assertThat(delegate.lastStatus()).isEqualTo(status);
    }

    @Test
    void displayStatusInProgressDoesNotTriggerTheEffect() {
        FakeGameView delegate = new FakeGameView();
        AtomicInteger triggerCount = new AtomicInteger();
        GameView decorator = new WinEffectGameView(delegate, triggerCount::incrementAndGet);

        decorator.displayStatus(new StatusViewModel("X's turn", GameOutcomeKind.IN_PROGRESS));

        assertThat(triggerCount.get()).isZero();
    }

    @Test
    void displayStatusDrawDoesNotTriggerTheEffect() {
        FakeGameView delegate = new FakeGameView();
        AtomicInteger triggerCount = new AtomicInteger();
        GameView decorator = new WinEffectGameView(delegate, triggerCount::incrementAndGet);

        decorator.displayStatus(new StatusViewModel("Draw!", GameOutcomeKind.DRAW));

        assertThat(triggerCount.get()).isZero();
    }

    @Test
    void effectsStackWhenMultipleDecoratorsWrapTheSameView() {
        FakeGameView delegate = new FakeGameView();
        AtomicInteger confettiTriggers = new AtomicInteger();
        AtomicInteger fireworksTriggers = new AtomicInteger();
        GameView decorator = new WinEffectGameView(
            new WinEffectGameView(delegate, confettiTriggers::incrementAndGet),
            fireworksTriggers::incrementAndGet);

        decorator.displayStatus(new StatusViewModel("X wins!", GameOutcomeKind.WIN));

        assertThat(confettiTriggers.get()).isEqualTo(1);
        assertThat(fireworksTriggers.get()).isEqualTo(1);
    }
}
