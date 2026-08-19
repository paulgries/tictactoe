package persistence.save_game.use_case;

/**
 * The input data for the Save Game Use Case. The session to save is read
 * from the application-layer session by the interactor, so the controller
 * has no view input to forward.
 */
public record SaveGameInputData() {
}