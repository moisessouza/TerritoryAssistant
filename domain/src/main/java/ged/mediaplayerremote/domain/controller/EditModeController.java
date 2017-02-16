package ged.mediaplayerremote.domain.controller;

import rx.Observable;
import rx.subjects.BehaviorSubject;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * A class that awaits for requests to enable or disable UI edit mode, and rebroadcasts current state to all relevant UI
 * components to keep them synchronized.
 */
@Singleton
public class EditModeController {

    private BehaviorSubject<Boolean> editModePublisher = BehaviorSubject.create(false);

    @Inject
    public EditModeController() {
        System.out.println("controller created");
    }

    /**
     * Request edit mode to be enabled.
     */
    public void enableEditMode() {
        editModePublisher.onNext(true);
    }

    /**
     * Request edit mode to be disabled.
     */
    public void disableEditMode() {
        editModePublisher.onNext(false);
    }

    /**
     * Get an {@link Observable} that will emit current edit mode state, and all subsequent changes.
     */
    public Observable getEditModeUpdates() {
        return editModePublisher;
    }
}
