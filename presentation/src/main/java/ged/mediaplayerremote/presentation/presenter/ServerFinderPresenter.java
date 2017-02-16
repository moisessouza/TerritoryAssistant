package ged.mediaplayerremote.presentation.presenter;

import ged.mediaplayerremote.domain.interactor.DefaultSubscriber;
import ged.mediaplayerremote.domain.interactor.UseCase;
import ged.mediaplayerremote.domain.model.MpcServer;
import ged.mediaplayerremote.presentation.dagger2.PerFragment;
import ged.mediaplayerremote.presentation.model.ServerModel;
import ged.mediaplayerremote.presentation.model.mapper.ServerModelMapper;
import ged.mediaplayerremote.presentation.view.ServerFinderView;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * {@link Presenter} that controls communication with {@link ServerFinderView}
 */
@PerFragment
public class ServerFinderPresenter implements Presenter {

    private UseCase findServersUseCase;

    private ServerFinderView serverFinderView;
    private ServerModelMapper serverModelMapper;

    @Inject
    public ServerFinderPresenter(@Named("serverFinder") UseCase findServersUseCase,
                                 ServerModelMapper mapper) {
        this.findServersUseCase = findServersUseCase;
        this.serverModelMapper = mapper;
    }

    public void initialize() {
        if (findServersUseCase.isUnsubscribed()) {
            findServers();
        }
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {
        findServersUseCase.unsubscribe();
        serverFinderView = null;
    }


    public void setView(ServerFinderView view) {
        this.serverFinderView = view;
    }

    public void onServerChosen(String ip) {
        serverFinderView.selectServer(ip);
    }

    public void onFloatingActionButtonClicked() {
        findServers();
    }


    private void findServers() {
        if (findServersUseCase.isSubscribed()) {
            findServersUseCase.unsubscribe();
        }
        serverFinderView.showViewLoading(256);
        serverFinderView.clearServers();
        serverFinderView.clearProgress();
        findServersUseCase.execute(new ServerSubscriber());
    }

    private class ServerSubscriber extends DefaultSubscriber<MpcServer> {

        int serversReceived = 0;

        @Override
        public void onCompleted() {
            serverFinderView.hideViewLoading();
        }

        @Override
        public void onError(Throwable e) {
            serverFinderView.hideViewLoading();
            serverFinderView.showMessage(e.getMessage());
        }

        @Override
        public void onNext(MpcServer mpcServer) {
            ServerModel serverModel = serverModelMapper.transform(mpcServer);
            serverFinderView.addProgress();
            if (serverModel != null) {
                serverFinderView.addServer(serverModel);
            }
            serversReceived++;
        }
    }
}
