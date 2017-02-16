package ged.mediaplayerremote.domain.interactor.command;

import ged.mediaplayerremote.domain.controller.CommandDispatcher;
import ged.mediaplayerremote.domain.executor.PostExecutionThread;
import ged.mediaplayerremote.domain.executor.ThreadExecutor;
import ged.mediaplayerremote.domain.interactor.UseCaseParametrized;
import rx.Observable;

import javax.inject.Inject;

/**
 * An {@link UseCaseParametrized} for sending a command to set volume to desired value. The value should be passed as a
 * parameter.
 */
public class SetVolumeUseCase extends UseCaseParametrized<Integer> {

    private CommandDispatcher commandDispatcher;

    @Inject
    public SetVolumeUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
                            CommandDispatcher commandDispatcher) {
        super(threadExecutor, postExecutionThread);
        this.commandDispatcher = commandDispatcher;
    }

    @Override
    protected Observable buildUseCaseObservable(Integer volume) {
        return commandDispatcher.setVolume(volume);
    }
}
