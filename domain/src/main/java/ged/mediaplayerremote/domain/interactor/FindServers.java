package ged.mediaplayerremote.domain.interactor;

import ged.mediaplayerremote.domain.executor.PostExecutionThread;
import ged.mediaplayerremote.domain.executor.ThreadExecutor;
import ged.mediaplayerremote.domain.repository.ServerRepository;
import rx.Observable;

import javax.inject.Inject;


/**
 * An {@link UseCase} implementation that will search for available MPC-HC servers.
 */
public class FindServers extends UseCase {

    private ServerRepository serverRepository;

    @Inject
    public FindServers(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
                       ServerRepository serverRepository) {
        super(threadExecutor, postExecutionThread);
        this.serverRepository = serverRepository;
    }

    protected Observable buildUseCaseObservable() {
        return this.serverRepository.getServers();
    }
}
