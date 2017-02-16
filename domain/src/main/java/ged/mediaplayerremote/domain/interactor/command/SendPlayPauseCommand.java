package ged.mediaplayerremote.domain.interactor.command;

import ged.mediaplayerremote.domain.controller.CommandDispatcher;
import ged.mediaplayerremote.domain.executor.PostExecutionThread;
import ged.mediaplayerremote.domain.executor.ThreadExecutor;
import ged.mediaplayerremote.domain.interactor.UseCase;
import rx.Observable;

import javax.inject.Inject;

/**
 * An {@link UseCase} for sending a play or pause command to the MPC-HC server. The actual command sent depends on
 * current {@link ged.mediaplayerremote.domain.model.PlaybackState}.
 */
public class SendPlayPauseCommand extends UseCase {

    private CommandDispatcher commandDispatcher;

    @Inject
    public SendPlayPauseCommand(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
                                CommandDispatcher commandDispatcher) {
        super(threadExecutor, postExecutionThread);
        this.commandDispatcher = commandDispatcher;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return commandDispatcher.playPause();
    }
}
