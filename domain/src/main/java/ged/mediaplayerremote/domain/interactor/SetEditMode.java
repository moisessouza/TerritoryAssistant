package ged.mediaplayerremote.domain.interactor;

import ged.mediaplayerremote.domain.controller.EditModeController;
import ged.mediaplayerremote.domain.executor.PostExecutionThread;
import ged.mediaplayerremote.domain.executor.ThreadExecutor;
import rx.Observable;

import javax.inject.Inject;

/**
 * An {@link UseCaseParametrized} to request edit mode to be enabled or disabled. Desired state should be passed as a
 * {@link Boolean} parameter.
 */
public class SetEditMode extends UseCaseParametrized<Boolean> {
    private EditModeController editModeController;

    @Inject
    public SetEditMode(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
                       EditModeController editModeController) {
        super(threadExecutor, postExecutionThread);
        this.editModeController = editModeController;
    }

    @Override
    protected Observable buildUseCaseObservable(Boolean editMode) {
        if (editMode)
            editModeController.enableEditMode();
        else
            editModeController.disableEditMode();

        return Observable.empty();
    }
}
