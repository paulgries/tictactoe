package game.save_game;

import static org.assertj.core.api.Assertions.assertThat;

import game.GameRenderState;
import game.GameViewModel;
import game.save_game.use_case.SaveGameOutputData;
import java.beans.PropertyChangeListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SaveGamePresenterTest {

    private GameViewModel gameViewModel;
    private SaveGamePresenter presenter;
    private int fireCount;

    @BeforeEach
    void setUp() {
        gameViewModel = new GameViewModel();
        PropertyChangeListener listener = evt -> fireCount++;
        gameViewModel.addPropertyChangeListener(listener);
        presenter = new SaveGamePresenter(gameViewModel);
    }

    @Test
    void prepareSuccessView_SetsConfirmationMessageAndFires() {
        presenter.prepareSuccessView(new SaveGameOutputData());

        assertThat(gameViewModel.getState().getMessage()).isEqualTo("Game saved");
        assertThat(fireCount).isEqualTo(1);
    }

    @Test
    void prepareFailView_SetsErrorMessageAndFires() {
        presenter.prepareFailView("could not save the game: boom");

        GameRenderState render = gameViewModel.getState();
        assertThat(render.getMessage()).isEqualTo("could not save the game: boom");
        assertThat(fireCount).isEqualTo(1);
    }
}