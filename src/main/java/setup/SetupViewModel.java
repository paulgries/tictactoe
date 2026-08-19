package setup;

import framework.ViewModel;

/**
 * The ViewModel for the setup screen, following the CAWithBuilder pattern of
 * one ViewModel per view: the panel binds to it, writes its widgets' values
 * into the {@link SetupState}, and displays transient messages the
 * presenters put there.
 */
public class SetupViewModel extends ViewModel<SetupState> {

    public SetupViewModel() {
        super("setup");
        setState(new SetupState());
    }
}