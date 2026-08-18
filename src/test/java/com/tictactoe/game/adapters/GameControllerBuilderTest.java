package com.tictactoe.game.adapters;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

class GameControllerBuilderTest {

    @Test
    void buildWithoutViewThrows() {
        GameControllerBuilder builder = new GameControllerBuilder()
            .uiScheduler(mock(UiScheduler.class));

        assertThatThrownBy(builder::build).isInstanceOf(NullPointerException.class);
    }

    @Test
    void buildWithoutUiSchedulerThrows() {
        GameControllerBuilder builder = new GameControllerBuilder()
            .view(mock(GameView.class));

        assertThatThrownBy(builder::build).isInstanceOf(NullPointerException.class);
    }
}