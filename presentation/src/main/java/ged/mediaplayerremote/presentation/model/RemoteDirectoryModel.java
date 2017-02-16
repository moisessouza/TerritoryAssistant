package ged.mediaplayerremote.presentation.model;


import java.util.List;

/**
 * Class representing directory located on remote PC with associated files. Used exclusively in presentation layer.
 */
public class RemoteDirectoryModel {
    private List<RemoteFileModel> files;
    private String location;

    public RemoteDirectoryModel(String location) {
        this.location = location;
    }

    public List<RemoteFileModel> getFiles() {
        return files;
    }

    public void setFiles(List<RemoteFileModel> files) {
        this.files = files;
    }

    public String getLocation() {
        return location;
    }
}
