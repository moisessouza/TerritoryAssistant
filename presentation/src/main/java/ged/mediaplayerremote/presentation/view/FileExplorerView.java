package ged.mediaplayerremote.presentation.view;


import ged.mediaplayerremote.presentation.model.RemoteFileModel;

import java.util.List;

/**
 * Interface representing a View in a model view presenter pattern.
 * This view represents file explorer, that is used to browse and load remote files to the player.
 */
public interface FileExplorerView extends BaseView {

    /**
     * Render list of files in the UI.
     *
     * @param files A list of {@link RemoteFileModel} that will be shown
     */
    void renderFiles(List<RemoteFileModel> files);

    /**
     * Display location in the header of the view.
     *
     * @param location A string with location.
     */
    void showLocation(String location);

    /**
     * Show a dialog, that will ask user to confirm selected {@link RemoteFileModel} before loading it into the player.
     *
     * @param remoteFile A file that will be loaded, if user confirms.
     */
    void showConfirmFileDialog(RemoteFileModel remoteFile);

    /**
     * Show a view with a progress bar indicating a loading process.
     */
    void showViewLoading();

    /**
     * Hide a loading view.
     */
    void hideViewLoading();

    /**
     * Show a message that will confirm a file being loaded successfully into the player.
     *
     * @param remoteFile A file that was loaded.
     */
    void showFileLoadedMessage(RemoteFileModel remoteFile);
}
