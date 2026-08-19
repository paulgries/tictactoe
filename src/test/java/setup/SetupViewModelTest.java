package setup;

import static org.assertj.core.api.Assertions.assertThat;

import game.domain.AiDifficulty;
import game.domain.GameMode;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class SetupViewModelTest {

    @Test
    void constructor_Defaults_AreSane() {
        SetupViewModel viewModel = new SetupViewModel();

        assertThat(viewModel.getViewName()).isEqualTo("setup");
        SetupState state = viewModel.getState();
        assertThat(state.getBoardSize()).isEqualTo(3);
        assertThat(state.getWinLength()).isEqualTo(3);
        assertThat(state.getMode()).isEqualTo(GameMode.TWO_PLAYER);
        assertThat(state.getDifficulty()).isEmpty();
        assertThat(state.isConfettiEnabled()).isTrue();
        assertThat(state.isFireworksEnabled()).isTrue();
        assertThat(state.isMarksEnabled()).isTrue();
        assertThat(state.getMessage()).isNull();
    }

    @Test
    void firePropertyChanged_StateChanged_DeliversCurrentState() {
        SetupViewModel viewModel = new SetupViewModel();
        List<PropertyChangeEvent> events = new ArrayList<>();
        viewModel.addPropertyChangeListener(events::add);
        viewModel.getState().setDifficulty(Optional.of(AiDifficulty.EASY));

        viewModel.firePropertyChanged();

        assertThat(events).hasSize(1);
        assertThat(events.get(0).getNewValue()).isSameAs(viewModel.getState());
    }
}