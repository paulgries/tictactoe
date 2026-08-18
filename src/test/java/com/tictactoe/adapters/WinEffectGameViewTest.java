package com.tictactoe.adapters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.tictactoe.adapters.viewmodel.GameOutcomeKind;
import com.tictactoe.adapters.viewmodel.StatusViewModel;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class WinEffectGameViewTest {

    @Test
    void displayStatusWithWinTriggersTheEffectExactlyOnce() {
        GameView delegate = mock(GameView.class);
        AtomicInteger triggerCount = new AtomicInteger();
        GameView decorator = new WinEffectGameView(delegate, triggerCount::incrementAndGet);

        decorator.displayStatus(new StatusViewModel("X wins!", GameOutcomeKind.WIN));

        assertThat(triggerCount.get()).isEqualTo(1);
    }

    @Test
    void displayStatusWithWinStillDelegatesToTheWrappedView() {
        GameView delegate = mock(GameView.class);
        GameView decorator = new WinEffectGameView(delegate, () -> { });
        StatusViewModel status = new StatusViewModel("X wins!", GameOutcomeKind.WIN);

        decorator.displayStatus(status);

        verify(delegate).displayStatus(status);
    }

    @Test
    void displayStatusInProgressDoesNotTriggerTheEffect() {
        GameView delegate = mock(GameView.class);
        AtomicInteger triggerCount = new AtomicInteger();
        GameView decorator = new WinEffectGameView(delegate, triggerCount::incrementAndGet);

        decorator.displayStatus(new StatusViewModel("X's turn", GameOutcomeKind.IN_PROGRESS));

        assertThat(triggerCount.get()).isZero();
    }

    @Test
    void displayStatusDrawDoesNotTriggerTheEffect() {
        GameView delegate = mock(GameView.class);
        AtomicInteger triggerCount = new AtomicInteger();
        GameView decorator = new WinEffectGameView(delegate, triggerCount::incrementAndGet);

        decorator.displayStatus(new StatusViewModel("Draw!", GameOutcomeKind.DRAW));

        assertThat(triggerCount.get()).isZero();
    }

    @Test
    void effectsStackWhenMultipleDecoratorsWrapTheSameView() {
        GameView delegate = mock(GameView.class);
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