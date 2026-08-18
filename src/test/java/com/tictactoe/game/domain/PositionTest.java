package com.tictactoe.game.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class PositionTest {

    @Test
    void equalPositionsHaveEqualHashCode() {
        Position a = new Position(1, 2);
        Position b = new Position(1, 2);

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void constructorRejectsNegativeRowOrColumn() {
        assertThatThrownBy(() -> new Position(-1, 0))
            .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new Position(0, -1))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
