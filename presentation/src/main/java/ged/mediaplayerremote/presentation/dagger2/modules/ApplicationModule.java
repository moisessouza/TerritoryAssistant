package ged.mediaplayerremote.presentation.dagger2.modules;

import android.content.Context;
import dagger.Module;
import dagger.Provides;
import ged.mediaplayerremote.AndroidApplication;
import ged.mediaplayerremote.UIThread;
import ged.mediaplayerremote.data.entity.mapper.RemoteDirectoryEntityDataMapper;
import ged.mediaplayerremote.data.entity.mapper.ServerEntityDataMapper;
import ged.mediaplayerremote.data.entity.mapper.StatusEntityDataMapper;
import ged.mediaplayerremote.data.executor.JobExecutor;
import ged.mediaplayerremote.data.network.MpcApi;
import ged.mediaplayerremote.data.parser.HtmlParser;
import ged.mediaplayerremote.data.repository.ButtonDataRepository;
import ged.mediaplayerremote.data.repository.MpcClientRepositoryImpl;
import ged.mediaplayerremote.data.repository.ServerDataRepository;
import ged.mediaplayerremote.data.repository.SharedPreferencesRepositoryImpl;
import ged.mediaplayerremote.data.repository.datasource.ButtonDataStore;
import ged.mediaplayerremote.data.repository.datasource.SharedPrefsButtonDataStore;

import ged.mediaplayerremote.domain.controller.PlaybackStatusProxy;
import ged.mediaplayerremote.domain.controller.ServerSettingsChangedListener;
import ged.mediaplayerremote.domain.executor.PostExecutionThread;
import ged.mediaplayerremote.domain.executor.ThreadExecutor;
import ged.mediaplayerremote.domain.repository.ButtonRepository;
import ged.mediaplayerremote.domain.repository.MpcClientRepository;
import ged.mediaplayerremote.domain.repository.ServerRepository;
import ged.mediaplayerremote.domain.repository.UserPreferencesRepository;
import ged.mediaplayerremote.presentation.view.widget.WidgetProvider;
import ged.mediaplayerremote.presentation.view.widget.WidgetStatusListener;

import javax.inject.Singleton;

/**
 * Dagger2 application module.
 */
@Module
public class ApplicationModule {

    private final AndroidApplication application;

    public ApplicationModule(AndroidApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return application;
    }

    @Provides
    @Singleton
    MpcApi provideMPCApi(UserPreferencesRepository userPreferencesRepository, HtmlParser htmlParser) {
        return new MpcApi(userPreferencesRepository, htmlParser, application.getResources());
    }

    @Provides
    @Singleton
    ThreadExecutor provideThreadExecutor(JobExecutor jobExecutor) {
        return jobExecutor;
    }

    @Provides
    @Singleton
    PostExecutionThread providePostExecutionThread(UIThread uiThread) {
        return uiThread;
    }

    @Provides
    @Singleton
    ButtonRepository provideButtonRepository(ButtonDataRepository buttonDataRepository) {
        return buttonDataRepository;
    }

    @Provides
    @Singleton
    ButtonDataStore buttonDataStore(SharedPrefsButtonDataStore sharedPrefsButtonDataStore) {
        return sharedPrefsButtonDataStore;
    }

    @Provides
    @Singleton
    ServerRepository provideServerRepository(ServerDataRepository serverDataRepository) {
        return serverDataRepository;
    }

    @Provides
    @Singleton
    UserPreferencesRepository provideUserPreferencesRepository(SharedPreferencesRepositoryImpl sharedPreferencesRepository) {
        return sharedPreferencesRepository;
    }

    @Provides
    @Singleton
    MpcClientRepository provideClientMpc(MpcApi mpcApi, ServerEntityDataMapper serverEntityDataMapper,
                                         StatusEntityDataMapper statusEntityDataMapper,
                                         RemoteDirectoryEntityDataMapper remoteDirectoryEntityDataMapper) {
        return new MpcClientRepositoryImpl(mpcApi, serverEntityDataMapper, statusEntityDataMapper,
                remoteDirectoryEntityDataMapper);
    }

    @Provides
    @Singleton
    PlaybackStatusProxy providePlaybackStatusProxy(MpcClientRepository mpcClientRepository,
                                                   UserPreferencesRepository userPreferencesRepository,
                                                   ThreadExecutor threadExecutor,
                                                   PostExecutionThread postExecutionThread) {

        return new PlaybackStatusProxy(mpcClientRepository, userPreferencesRepository, threadExecutor,
                postExecutionThread, application);
    }

    @Provides
    @Singleton
    WidgetStatusListener provideWidgetStatusListener(WidgetProvider widgetProvider) {
        return widgetProvider;
    }

    @Provides
    @Singleton
    ServerSettingsChangedListener provideServerSettingsChangedListener(PlaybackStatusProxy playbackStatusProxy) {
        return playbackStatusProxy;
    }
}
