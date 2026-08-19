package game.make_human_move;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import game.domain.Position;
import game.make_human_move.use_case.MakeHumanMoveInputBoundary;
import game.make_human_move.use_case.MakeHumanMoveInputData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MakeHumanMoveControllerTest {

    @Mock
    private MakeHumanMoveInputBoundary makeHumanMoveUseCase;

    private MakeHumanMoveController controller;

    @BeforeEach
    void setUp() {
        controller = new MakeHumanMoveController(makeHumanMoveUseCase);
    }

    @Test
    void execute_DelegatesClickedCell() {
        controller.execute(1, 2);

        ArgumentCaptor<MakeHumanMoveInputData> captor =
            ArgumentCaptor.forClass(MakeHumanMoveInputData.class);
        verify(makeHumanMoveUseCase).execute(captor.capture());
        assertThat(captor.getValue().position()).isEqualTo(new Position(1, 2));
    }
}