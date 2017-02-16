package ged.mediaplayerremote.domain.interactor.command;

import ged.mediaplayerremote.domain.controller.CommandDispatcher;
import ged.mediaplayerremote.domain.executor.PostExecutionThread;
import ged.mediaplayerremote.domain.executor.ThreadExecutor;
import ged.mediaplayerremote.domain.interactor.UseCase;
import ged.mediaplayerremote.domain.interactor.UseCaseParametrized;
import rx.Observable;

import javax.inject.Inject;
import java.util.Map;

/**
 * An {@link UseCase} for sending various commands to the MPC-HC server. Desired command should be passed as a
 * parameter.
 */
public class SendMiscCommand extends UseCaseParametrized<Map<String, String>> {
    private CommandDispatcher commandDispatcher;

    @Inject
    public SendMiscCommand(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
                           CommandDispatcher commandDispatcher) {
        super(threadExecutor, postExecutionThread);
        this.commandDispatcher = commandDispatcher;
    }

    protected Observable buildUseCaseObservable(Map<String, String> command) {
        return commandDispatcher.miscCommand(command);
    }

}
