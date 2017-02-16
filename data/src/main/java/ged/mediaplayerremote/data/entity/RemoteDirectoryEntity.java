package ged.mediaplayerremote.data.entity;


import java.util.List;

/**
 * Class representing directory located on remote PC with associated files. Used exclusively in data layer.
 */
public class RemoteDirectoryEntity {

    private List<RemoteFileEntity> files;
    private String location;

    public RemoteDirectoryEntity(String location) {
        this.location = location;
    }

    public List<RemoteFileEntity> getFiles() {
        return files;
    }

    public void setFiles(List<RemoteFileEntity> files) {
        this.files = files;
    }

    public String getLocation() {
        return location;
    }


}

