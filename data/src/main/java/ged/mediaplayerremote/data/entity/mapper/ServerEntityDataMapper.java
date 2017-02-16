package ged.mediaplayerremote.data.entity.mapper;

import ged.mediaplayerremote.data.entity.ServerEntity;
import ged.mediaplayerremote.domain.model.MpcServer;

import javax.inject.Inject;

/**
 * Mapper class used to transform {@link ServerEntity} (in the data layer) to {@link MpcServer} in the
 * domain layer and vice versa.
 */
public class ServerEntityDataMapper {
    @Inject
    public ServerEntityDataMapper() {
    }

    /**
     * Transform a {@link ServerEntity} into an {@link MpcServer}.
     *
     * @param serverEntity Object to be transformed.
     * @return {@link MpcServer} given valid {@link ServerEntity} otherwise null.
     */
    public MpcServer transform(ServerEntity serverEntity) {

        MpcServer mpcServer = null;
        if (serverEntity != null) {
            mpcServer = new MpcServer();
            mpcServer.setIp(serverEntity.getIp());
            mpcServer.setHostName(serverEntity.getHostName());
            mpcServer.setPort(serverEntity.getPort());
            mpcServer.setConnectionTimeout(serverEntity.getConnectionTimeout());
        }

        return mpcServer;
    }

    /**
     * Transform a {@link MpcServer} into an {@link ServerEntity}.
     *
     * @param mpcServer Object to be transformed.
     * @return {@link ServerEntity} given valid {@link MpcServer} otherwise null.
     */
    public ServerEntity transform(MpcServer mpcServer) {

        ServerEntity serverEntity = null;
        if (mpcServer != null) {
            serverEntity = new ServerEntity();
            serverEntity.setHostName(mpcServer.getHostName());
            serverEntity.setIp(mpcServer.getIp());
            serverEntity.setPort(mpcServer.getPort());
            serverEntity.setConnectionTimeout(mpcServer.getConnectionTimeout());
        }

        return serverEntity;
    }
}
