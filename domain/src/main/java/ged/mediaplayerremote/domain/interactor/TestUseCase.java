package ged.mediaplayerremote.domain.interactor;

import ged.mediaplayerremote.domain.executor.PostExecutionThread;
import ged.mediaplayerremote.domain.executor.ThreadExecutor;
import ged.mediaplayerremote.domain.model.MpcServer;
import ged.mediaplayerremote.domain.repository.MpcClientRepository;
import rx.Observable;

import javax.inject.Inject;

/**
 * Created by Szef on 2016-12-11.
 */

public class TestUseCase extends UseCase
{

    private MpcClientRepository mpcClientRepository;

    @Inject
    public TestUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
                        MpcClientRepository mpcClientRepository)
    {
        super(threadExecutor, postExecutionThread);
        this.mpcClientRepository = mpcClientRepository;
    }



    @Override
    protected Observable buildUseCaseObservable()
    {
        MpcServer mpcServer = new MpcServer();
        mpcServer.setConnectionTimeout(1000);
        mpcServer.setIp("192.168.1.121");
        mpcServer.setPort("13579");
        return mpcClientRepository.getPlaybackStatusUpdates(mpcServer);
    }
}
