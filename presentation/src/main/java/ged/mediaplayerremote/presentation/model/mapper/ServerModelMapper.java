package ged.mediaplayerremote.presentation.model.mapper;

import ged.mediaplayerremote.domain.model.MpcServer;
import ged.mediaplayerremote.presentation.model.ServerModel;

import javax.inject.Inject;

/**
 * Mapper class used to transform {@link ServerModel} (in the presentation layer) to {@link MpcServer} in the
 * domain layer and vice versa.
 */
public class ServerModelMapper {

    @Inject
    public ServerModelMapper() {
    }

    /**
     * Transform a {@link MpcServer} into an {@link ServerModel}.
     *
     * @param mpcServer Object to be transformed.
     * @return {@link ServerModel} given valid {@link MpcServer} otherwise null.
     */
    public ServerModel transform(MpcServer mpcServer) {
        ServerModel serverModel = null;
        if (mpcServer != null) {
            serverModel = new ServerModel();
            serverModel.setIp(mpcServer.getIp());
            serverModel.setHostName(mpcServer.getHostName());
            serverModel.setPort(mpcServer.getPort());
            serverModel.setConnectionTimeout(mpcServer.getConnectionTimeout());
        }

        return serverModel;
    }

    /**
     * Transform a {@link ServerModel} into an {@link MpcServer}.
     *
     * @param serverModel Object to be transformed.
     * @return {@link MpcServer} given valid {@link ServerModel} otherwise null.
     */
    public MpcServer transform(ServerModel serverModel) {
        MpcServer mpcServer = null;
        if (serverModel != null) {
            mpcServer = new MpcServer();
            mpcServer.setIp(serverModel.getIp());
            mpcServer.setHostName(serverModel.getHostName());
            mpcServer.setPort(serverModel.getPort());
            mpcServer.setConnectionTimeout(serverModel.getConnectionTimeout());
        }

        return mpcServer;
    }
}
