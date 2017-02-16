package ged.mediaplayerremote.domain.interactor.command;

import ged.mediaplayerremote.domain.controller.CommandDispatcher;
import ged.mediaplayerremote.domain.executor.PostExecutionThread;
import ged.mediaplayerremote.domain.executor.ThreadExecutor;
import ged.mediaplayerremote.domain.interactor.UseCase;
import rx.Observable;

import javax.inject.Inject;

/**
 * An {@link UseCase} for sending a rewind command to the MPC-HC server. Jump position depends on user settings
 * and is taken from {@link ged.mediaplayerremote.domain.repository.UserPreferencesRepository}
 */
public class SendRewindCommand extends UseCase {

    private CommandDispatcher commandDispatcher;

    @Inject
    public SendRewindCommand(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
                             CommandDispatcher commandDispatcher) {
        super(threadExecutor, postExecutionThread);
        this.commandDispatcher = commandDispatcher;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return commandDispatcher.rewind();
    }
}
