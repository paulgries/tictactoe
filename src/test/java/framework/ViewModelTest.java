package framework;

import static org.assertj.core.api.Assertions.assertThat;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ViewModelTest {

    @Test
    void firePropertyChanged_DefaultOverload_ReportsStateProperty() {
        ViewModel<String> viewModel = new ViewModel<>("test");
        List<PropertyChangeEvent> events = new ArrayList<>();
        viewModel.addPropertyChangeListener(events::add);
        viewModel.setState("value");

        viewModel.firePropertyChanged();

        assertThat(events).hasSize(1);
        assertThat(events.get(0).getPropertyName()).isEqualTo("state");
        assertThat(events.get(0).getNewValue()).isEqualTo("value");
    }

    @Test
    void firePropertyChanged_NamedOverload_ReportsGivenProperty() {
        ViewModel<String> viewModel = new ViewModel<>("test");
        List<PropertyChangeEvent> events = new ArrayList<>();
        viewModel.addPropertyChangeListener(events::add);
        viewModel.setState("value");

        viewModel.firePropertyChanged("session");

        assertThat(events).hasSize(1);
        assertThat(events.get(0).getPropertyName()).isEqualTo("session");
        assertThat(events.get(0).getNewValue()).isEqualTo("value");
    }

    @Test
    void addPropertyChangeListener_TwoListeners_BothReceiveEvent() {
        ViewModel<String> viewModel = new ViewModel<>("test");
        List<PropertyChangeEvent> events = new ArrayList<>();
        viewModel.addPropertyChangeListener(events::add);
        viewModel.addPropertyChangeListener(events::add);

        viewModel.firePropertyChanged();

        assertThat(events).hasSize(2);
    }
}