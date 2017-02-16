package ged.mediaplayerremote.data.repository;

import ged.mediaplayerremote.data.entity.mapper.ServerEntityDataMapper;
import ged.mediaplayerremote.data.network.ServerFinder;
import ged.mediaplayerremote.domain.model.MpcServer;
import ged.mediaplayerremote.domain.repository.ServerRepository;
import rx.Observable;

import javax.inject.Inject;

/**
 * A {@link ServerRepository} for retrieving {@link MpcServer}.
 */
public class ServerDataRepository implements ServerRepository {
    private ServerEntityDataMapper serverEntityDataMapper;
    private ServerFinder serverFinder;

    @Inject
    public ServerDataRepository(ServerEntityDataMapper serverEntityDataMapper, ServerFinder serverFinder) {
        this.serverEntityDataMapper = serverEntityDataMapper;
        this.serverFinder = serverFinder;
    }

    @Override
    public Observable<MpcServer> getServers() {
        return serverFinder.getServers().map(serverEntity -> serverEntityDataMapper.transform(serverEntity));
    }


}
