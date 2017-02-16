package ged.mediaplayerremote.domain.interactor;

import ged.mediaplayerremote.domain.controller.EditModeController;
import ged.mediaplayerremote.domain.executor.PostExecutionThread;
import ged.mediaplayerremote.domain.executor.ThreadExecutor;
import rx.Observable;

import javax.inject.Inject;

/**
 * Subscribe to this {@link UseCase} to get current state of edit mode, and all subsequent updates.
 */
public class GetEditModeUpdates extends UseCase {

    private EditModeController editModeController;

    @Inject
    public GetEditModeUpdates(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
                              EditModeController editModeController) {
        super(threadExecutor, postExecutionThread);
        this.editModeController = editModeController;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return editModeController.getEditModeUpdates();
    }
}
