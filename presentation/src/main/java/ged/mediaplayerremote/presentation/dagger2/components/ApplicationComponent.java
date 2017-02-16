package ged.mediaplayerremote.presentation.dagger2.components;

import android.content.Context;
import dagger.Component;
import ged.mediaplayerremote.AndroidApplication;
import ged.mediaplayerremote.domain.controller.CommandDispatcher;
import ged.mediaplayerremote.domain.controller.EditModeController;
import ged.mediaplayerremote.domain.controller.ServerSettingsChangedListener;
import ged.mediaplayerremote.domain.executor.PostExecutionThread;
import ged.mediaplayerremote.domain.executor.ThreadExecutor;
import ged.mediaplayerremote.domain.interactor.UseCase;
import ged.mediaplayerremote.domain.repository.ButtonRepository;
import ged.mediaplayerremote.domain.repository.MpcClientRepository;
import ged.mediaplayerremote.domain.repository.ServerRepository;
import ged.mediaplayerremote.domain.repository.UserPreferencesRepository;
import ged.mediaplayerremote.presentation.dagger2.modules.ApplicationModule;
import ged.mediaplayerremote.presentation.dagger2.modules.WidgetInteractorModule;
import ged.mediaplayerremote.presentation.model.mapper.PlaybackStatusModelMapper;
import ged.mediaplayerremote.presentation.model.mapper.RemoteDirectoryModelMapper;
import ged.mediaplayerremote.presentation.model.mapper.RemoteFileModelMapper;
import ged.mediaplayerremote.presentation.view.activity.BaseActivity;
import ged.mediaplayerremote.presentation.view.widget.WidgetStatusListener;

import javax.inject.Named;
import javax.inject.Singleton;


@Singleton
@Component(modules = {ApplicationModule.class, WidgetInteractorModule.class})
public interface ApplicationComponent {

    void inject(AndroidApplication androidApplication);
    void inject(BaseActivity baseActivity);

    Context context();
    MpcClientRepository CLIENT_MPC();
    ThreadExecutor threadExecutor();
    PostExecutionThread postExecutionThread();
    PlaybackStatusModelMapper PLAYBACK_STATUS_MODEL_MAPPER();
    ButtonRepository BUTTON_REPOSITORY();
    ServerRepository SERVER_REPOSITORY();
    UserPreferencesRepository USER_PREFERENCES_REPOSITORY();
    EditModeController EDIT_MODE_CONTROLLER();
    ServerSettingsChangedListener SERVER_SETTINGS_CHANGED_LISTENER();
    CommandDispatcher COMMAND_DISPATCHER();
    WidgetStatusListener WIDGET_STATUS_LISTENER();
    RemoteFileModelMapper REMOTE_FILE_MODEL_MAPPER();
    RemoteDirectoryModelMapper REMOTE_DIRECTORY_MODEL_MAPPER();

    @Named("playbackStatus") UseCase USE_CASE_PLAYBACK_STATUS();
    @Named("playPause") UseCase USE_CASE_PLAY_PAUSE();
    @Named("fastForward") UseCase USE_CASE_FAST_FORWARD();
    @Named("rewind") UseCase USE_CASE_REWIND();
    @Named("VolumeDown") UseCase USE_CASE_VOLUME_DOWN();
    @Named("VolumeUp") UseCase USE_CASE_VOLUME_UP();



}
