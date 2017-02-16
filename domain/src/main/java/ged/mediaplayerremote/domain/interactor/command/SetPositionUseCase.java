package ged.mediaplayerremote.domain.interactor.command;

import ged.mediaplayerremote.domain.controller.CommandDispatcher;
import ged.mediaplayerremote.domain.executor.PostExecutionThread;
import ged.mediaplayerremote.domain.executor.ThreadExecutor;
import ged.mediaplayerremote.domain.interactor.UseCaseParametrized;
import rx.Observable;

import javax.inject.Inject;

/**
 * An {@link UseCaseParametrized} for sending a "jump to position" command to the MPC-HC server. The jump position (in
 * milliseconds) should be passed as a parameter.
 */
public class SetPositionUseCase extends UseCaseParametrized<Long> {

    private CommandDispatcher commandDispatcher;

    @Inject
    public SetPositionUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
                              CommandDispatcher commandDispatcher) {
        super(threadExecutor, postExecutionThread);
        this.commandDispatcher = commandDispatcher;
    }

    @Override
    protected Observable buildUseCaseObservable(Long position) {
        return commandDispatcher.setPosition(position);
    }
}
