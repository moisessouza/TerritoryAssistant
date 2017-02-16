package ged.mediaplayerremote.domain.interactor;

import ged.mediaplayerremote.domain.executor.PostExecutionThread;
import ged.mediaplayerremote.domain.executor.ThreadExecutor;
import ged.mediaplayerremote.domain.model.MpcServer;
import ged.mediaplayerremote.domain.repository.MpcClientRepository;
import ged.mediaplayerremote.domain.repository.UserPreferencesRepository;
import rx.Observable;

import javax.inject.Inject;

/**
 * An {@link UseCase} that will periodically download image preview of whatever is displayed in MPC-HC player at the
 * moment.
 */
public class GetSnapshot extends UseCase {

    private MpcClientRepository mpcClientRepository;
    private UserPreferencesRepository userPreferencesRepository;

    @Inject
    public GetSnapshot(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
                       MpcClientRepository mpcClientRepository, UserPreferencesRepository userPreferencesRepository) {
        super(threadExecutor, postExecutionThread);
        this.mpcClientRepository = mpcClientRepository;
        this.userPreferencesRepository = userPreferencesRepository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        MpcServer server = new MpcServer();
        server.setIp(userPreferencesRepository.getIP());
        server.setPort(userPreferencesRepository.getPort());
        server.setConnectionTimeout(userPreferencesRepository.getConnectionTimeout());
        return mpcClientRepository.getSnapshot(server);
    }


}
