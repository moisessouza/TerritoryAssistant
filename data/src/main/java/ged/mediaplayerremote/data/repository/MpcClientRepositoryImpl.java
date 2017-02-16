package ged.mediaplayerremote.data.repository;

import ged.mediaplayerremote.data.entity.ServerEntity;
import ged.mediaplayerremote.data.entity.mapper.RemoteDirectoryEntityDataMapper;
import ged.mediaplayerremote.data.entity.mapper.ServerEntityDataMapper;
import ged.mediaplayerremote.data.entity.mapper.StatusEntityDataMapper;
import ged.mediaplayerremote.data.network.MpcApi;
import ged.mediaplayerremote.domain.model.MpcServer;
import ged.mediaplayerremote.domain.model.PlaybackStatus;
import ged.mediaplayerremote.domain.model.RemoteDirectory;
import ged.mediaplayerremote.domain.repository.MpcClientRepository;
import rx.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;


/**
 *  * A {@link MpcClientRepository} implementation for getting data from MPC-HC server, and sending commands to it.
 */
@Singleton
public class MpcClientRepositoryImpl implements MpcClientRepository {

    private MpcApi mpcApi;
    private StatusEntityDataMapper statusMapper;
    private ServerEntityDataMapper serverMapper;
    private RemoteDirectoryEntityDataMapper directoryMapper;

    @Inject
    public MpcClientRepositoryImpl(MpcApi mpcApi, ServerEntityDataMapper serverEntityDataMapper,
                                   StatusEntityDataMapper statusEntityDataMapper,
                                   RemoteDirectoryEntityDataMapper directoryMapper) {
        this.mpcApi = mpcApi;
        this.serverMapper = serverEntityDataMapper;
        this.statusMapper = statusEntityDataMapper;
        this.directoryMapper = directoryMapper;
    }

    @Override
    public Observable<Void> sendCommand(MpcServer server, Map<String, String> command) {
        ServerEntity serverEntity = serverMapper.transform(server);
        return mpcApi.sendRequest(serverEntity, command);
    }

    @Override
    public Observable<PlaybackStatus> getPlaybackStatusUpdates(MpcServer server) {
        ServerEntity serverEntity = serverMapper.transform(server);
        return mpcApi.getPlaybackStatusUpdates(serverEntity).map(statusEntity -> statusMapper.transform(statusEntity));
    }


    @Override
    public Observable<byte[]> getSnapshot(MpcServer server) {
        return mpcApi.getSnapshot(serverMapper.transform(server));
    }

    @Override
    public Observable<RemoteDirectory> getDirectory(MpcServer server, String location) {
        ServerEntity serverEntity = serverMapper.transform(server);
        return mpcApi.getDirectory(serverEntity, location)
                .map(remoteDirectory -> directoryMapper.transform(remoteDirectory));
    }
}

