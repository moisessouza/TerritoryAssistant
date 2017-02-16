package ged.mediaplayerremote.presentation.presenter;

import ged.mediaplayerremote.domain.interactor.DefaultSubscriber;
import ged.mediaplayerremote.domain.interactor.UseCaseParametrized;
import ged.mediaplayerremote.domain.model.RemoteDirectory;
import ged.mediaplayerremote.domain.model.RemoteFile;
import ged.mediaplayerremote.presentation.dagger2.PerFragment;
import ged.mediaplayerremote.presentation.model.RemoteDirectoryModel;
import ged.mediaplayerremote.presentation.model.RemoteFileModel;
import ged.mediaplayerremote.presentation.model.mapper.RemoteDirectoryModelMapper;
import ged.mediaplayerremote.presentation.model.mapper.RemoteFileModelMapper;
import ged.mediaplayerremote.presentation.view.FileExplorerView;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;

import static ged.mediaplayerremote.domain.model.FileType.DIRECTORY;

/**
 * {@link Presenter} that controls communication with {@link FileExplorerView}
 */
@PerFragment
public class FileExplorerPresenter implements Presenter {
    private FileExplorerView fileExplorerView;

    private RemoteDirectoryModelMapper mRemoteDirectoryModelMapper;
    private RemoteFileModelMapper mRemoteFileModelMapper;
    private UseCaseParametrized<RemoteFile> loadFileUseCase;

    private RemoteFileModel lastRequestedFile;

    @Inject
    public FileExplorerPresenter(@Named("loadFile") UseCaseParametrized<RemoteFile> loadFileUseCase,
                                 RemoteDirectoryModelMapper remoteDirectoryModelMapper,
                                 RemoteFileModelMapper remoteFileModelMapper) {

        this.loadFileUseCase = loadFileUseCase;
        this.mRemoteDirectoryModelMapper = remoteDirectoryModelMapper;
        this.mRemoteFileModelMapper = remoteFileModelMapper;
    }

    /**
     * Initializes the presenter by starting to retrieve remote files from the working directory.
     */
    public void initialize() {
        loadFile(null);
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {
        loadFileUseCase.unsubscribe();
        fileExplorerView = null;
    }

    public void setView(FileExplorerView fileExplorerView) {
        this.fileExplorerView = fileExplorerView;
    }


    public void onRefreshClicked() {
        loadFileUseCase.unsubscribe();
        fileExplorerView.renderFiles(Collections.emptyList());
        loadFile(lastRequestedFile);
    }

    public void onFileEntityClicked(RemoteFileModel remoteFile) {
        if (remoteFile.getType() == DIRECTORY) {
            loadFile(remoteFile);
        } else {
            fileExplorerView.showConfirmFileDialog(remoteFile);
        }
    }

    public void onFileConfirmed(RemoteFileModel remoteFileModel) {
        loadFile(remoteFileModel);
    }

    private void loadFile(RemoteFileModel remoteFileModel) {
        fileExplorerView.showViewLoading();
        lastRequestedFile = remoteFileModel;
        if (remoteFileModel == null || remoteFileModel.getType() == DIRECTORY) {
            loadFileUseCase.execute(new DirectorySubscriber(), mRemoteFileModelMapper.transform(remoteFileModel));
        } else {
            loadFileUseCase.execute(new FileSubscriber(), mRemoteFileModelMapper.transform(remoteFileModel));
        }
    }

    private class DirectorySubscriber extends DefaultSubscriber<RemoteDirectory> {
        @Override
        public void onNext(RemoteDirectory remoteDirectory) {
            RemoteDirectoryModel remoteDirectoryModel = mRemoteDirectoryModelMapper.transform(remoteDirectory);
            fileExplorerView.renderFiles(remoteDirectoryModel.getFiles());
            fileExplorerView.showLocation(remoteDirectoryModel.getLocation());
        }

        @Override
        public void onCompleted() {
            fileExplorerView.hideViewLoading();
        }

        @Override
        public void onError(Throwable e) {
            fileExplorerView.hideViewLoading();
            if (lastRequestedFile == null) {
                fileExplorerView.showLocation("---");
            }

            fileExplorerView.showMessage(e.getMessage());
        }
    }

    private class FileSubscriber extends DefaultSubscriber {
        @Override
        public void onCompleted() {
            fileExplorerView.hideViewLoading();
            fileExplorerView.showFileLoadedMessage(lastRequestedFile);
        }

        @Override
        public void onError(Throwable e) {
            fileExplorerView.hideViewLoading();
            fileExplorerView.showMessage(e.getMessage());
        }
    }


}
