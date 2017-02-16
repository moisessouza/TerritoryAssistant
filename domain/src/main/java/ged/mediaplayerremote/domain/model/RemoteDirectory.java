package ged.mediaplayerremote.domain.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing directory located on remote PC with associated files. Used exclusively in domain layer.
 */
public class RemoteDirectory
{
    private List<RemoteFile> files;
    private String location;

    public RemoteDirectory(String location)
    {
        this.location = location;
    }

    public List<RemoteFile> getFiles()
    {
        return files;
    }

    public void setFiles(List<RemoteFile> files)
    {
        this.files = files;
    }

    public String getLocation()
    {
        return location;
    }


}
