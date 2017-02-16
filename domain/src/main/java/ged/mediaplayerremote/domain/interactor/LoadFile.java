package ged.mediaplayerremote.domain.interactor;

import ged.mediaplayerremote.domain.executor.PostExecutionThread;
import ged.mediaplayerremote.domain.executor.ThreadExecutor;
import ged.mediaplayerremote.domain.model.RemoteFile;
import ged.mediaplayerremote.domain.model.MpcServer;
import ged.mediaplayerremote.domain.repository.MpcClientRepository;
import ged.mediaplayerremote.domain.repository.UserPreferencesRepository;
import rx.Observable;

import javax.inject.Inject;

/**
 * An {@link UseCaseParametrized} that will request MPC-HC player to load and play a {@link RemoteFile} specified as a
 * parameter. If the file is an instance of {@link ged.mediaplayerremote.domain.model.RemoteDirectory}, the server will
 * return it's content instead.
 */
public class LoadFile extends UseCaseParametrized<RemoteFile>
{
    private MpcClientRepository mpcClientRepository;
    private UserPreferencesRepository userPreferencesRepository;

    @Inject
    public LoadFile(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
                    MpcClientRepository mpcClientRepository, UserPreferencesRepository userPreferencesRepository)
    {
        super(threadExecutor, postExecutionThread);
        this.mpcClientRepository = mpcClientRepository;
        this.userPreferencesRepository = userPreferencesRepository;

    }

    @Override
    protected Observable buildUseCaseObservable(RemoteFile file)
    {
        MpcServer server = new MpcServer();
        server.setIp(userPreferencesRepository.getIP());
        server.setPort(userPreferencesRepository.getPort());
        server.setConnectionTimeout(userPreferencesRepository.getConnectionTimeout());
        String location;
        if (file == null)
            location = null;
        else
            location = file.getFilePath();

        return mpcClientRepository.getDirectory(server, location);
    }
}
