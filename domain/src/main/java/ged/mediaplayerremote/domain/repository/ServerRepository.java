package ged.mediaplayerremote.domain.repository;

import ged.mediaplayerremote.domain.model.MpcServer;
import rx.Observable;

/**
 * Interface that represents a repository for getting {@link MpcServer}.
 */
public interface ServerRepository {

    /**
     * Get an {@link Observable} which will consecutively emit  all available {@link MpcServer} in current network.
     */
    Observable<MpcServer> getServers();
}
