package ged.mediaplayerremote.domain.interactor.command;

import ged.mediaplayerremote.domain.controller.CommandDispatcher;
import ged.mediaplayerremote.domain.executor.PostExecutionThread;
import ged.mediaplayerremote.domain.executor.ThreadExecutor;
import ged.mediaplayerremote.domain.interactor.UseCase;
import rx.Observable;

import javax.inject.Inject;

/**
 * An {@link UseCase} for sending a volume up command to the MPC-HC server. New volume value depends on user settings
 * and is taken from {@link ged.mediaplayerremote.domain.repository.UserPreferencesRepository}
 */
public class SendVolumeUpCommand extends UseCase {

    private CommandDispatcher commandDispatcher;

    @Inject
    public SendVolumeUpCommand(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
                               CommandDispatcher commandDispatcher) {
        super(threadExecutor, postExecutionThread);
        this.commandDispatcher = commandDispatcher;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return commandDispatcher.raiseVolume();
    }
}
