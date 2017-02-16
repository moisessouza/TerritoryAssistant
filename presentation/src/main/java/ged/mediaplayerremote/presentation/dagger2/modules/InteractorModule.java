package ged.mediaplayerremote.presentation.dagger2.modules;

import dagger.Module;
import dagger.Provides;
import ged.mediaplayerremote.domain.controller.CommandDispatcher;
import ged.mediaplayerremote.domain.controller.EditModeController;
import ged.mediaplayerremote.domain.executor.PostExecutionThread;
import ged.mediaplayerremote.domain.executor.ThreadExecutor;
import ged.mediaplayerremote.domain.interactor.FindServers;
import ged.mediaplayerremote.domain.interactor.GetEditModeUpdates;
import ged.mediaplayerremote.domain.interactor.GetSnapshot;
import ged.mediaplayerremote.domain.interactor.LoadFile;
import ged.mediaplayerremote.domain.interactor.SetEditMode;
import ged.mediaplayerremote.domain.interactor.UseCase;
import ged.mediaplayerremote.domain.interactor.UseCaseParametrized;
import ged.mediaplayerremote.domain.interactor.command.SendMiscCommand;
import ged.mediaplayerremote.domain.interactor.command.SetPositionUseCase;
import ged.mediaplayerremote.domain.interactor.command.SetVolumeUseCase;
import ged.mediaplayerremote.domain.model.RemoteFile;
import ged.mediaplayerremote.domain.repository.MpcClientRepository;
import ged.mediaplayerremote.domain.repository.ServerRepository;
import ged.mediaplayerremote.domain.repository.UserPreferencesRepository;

import javax.inject.Named;
import java.util.Map;

/**
 * Dagger module that provides Use Cases for all presenters.
 */
@Module
public class InteractorModule {

    public InteractorModule() {
    }

    @Provides
    @Named("loadFile")
    UseCaseParametrized<RemoteFile> provideLoadFileUseCase(ThreadExecutor threadExecutor,
                                                           PostExecutionThread postExecutionThread,
                                                           MpcClientRepository mpcClientRepository,
                                                           UserPreferencesRepository userPreferencesRepository) {
        return new LoadFile(threadExecutor, postExecutionThread, mpcClientRepository, userPreferencesRepository);
    }

    @Provides
    @Named("setEditMode")
    UseCaseParametrized<Boolean> provideSetEditModeUseCase(ThreadExecutor threadExecutor,
                                                           PostExecutionThread postExecutionThread,
                                                           EditModeController editModeController) {
        return new SetEditMode(threadExecutor, postExecutionThread, editModeController);
    }

    @Provides
    @Named("editModeListener")
    UseCase provideEditModeListenerUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
                                           EditModeController editModeController) {
        return new GetEditModeUpdates(threadExecutor, postExecutionThread, editModeController);
    }

    @Provides
    @Named("snapshot")
    UseCase provideGetSnapshotUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
                                      MpcClientRepository mpcClientRepository,
                                      UserPreferencesRepository userPreferencesRepository) {
        return new GetSnapshot(threadExecutor, postExecutionThread, mpcClientRepository, userPreferencesRepository);
    }

    @Provides
    @Named("sendCommand")
    UseCaseParametrized<Map<String, String>> provideSendCommandUseCase(ThreadExecutor threadExecutor,
                                                                       PostExecutionThread postExecutionThread,
                                                                       CommandDispatcher commandDispatcher) {
        return new SendMiscCommand(threadExecutor, postExecutionThread, commandDispatcher);
    }

    @Provides
    @Named("SetPosition")
    UseCaseParametrized<Long> provideSetPositionUseCase(ThreadExecutor threadExecutor,
                                                        PostExecutionThread postExecutionThread,
                                                        CommandDispatcher commandDispatcher) {
        return new SetPositionUseCase(threadExecutor, postExecutionThread, commandDispatcher);
    }

    @Provides
    @Named("SetVolume")
    UseCaseParametrized<Integer> provideSetVolumeUseCase(ThreadExecutor threadExecutor,
                                                         PostExecutionThread postExecutionThread,
                                                         CommandDispatcher commandDispatcher) {
        return new SetVolumeUseCase(threadExecutor, postExecutionThread, commandDispatcher);
    }

    @Provides
    @Named("serverFinder")
    UseCase provideFindServersUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
                                      ServerRepository serverRepository) {
        return new FindServers(threadExecutor, postExecutionThread, serverRepository);
    }
}
