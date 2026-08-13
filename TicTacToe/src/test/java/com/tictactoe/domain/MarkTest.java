package com.tictactoe.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MarkTest {

    @Test
    void otherReturnsOForX() {
        assertThat(Mark.X.other()).isEqualTo(Mark.O);
    }

    @Test
    void otherReturnsXForO() {
        assertThat(Mark.O.other()).isEqualTo(Mark.X);
    }
}
