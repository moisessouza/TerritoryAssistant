package ged.mediaplayerremote.presentation.model;

import ged.mediaplayerremote.domain.model.FileType;

/**
 * Class representing file located on remote PC. Used in presentation layer.
 */
public class RemoteFileModel {
    private String filePath;
    private String fileName;
    private FileType type;

    public RemoteFileModel(String filePath)
    {
        this.filePath = filePath;
    }

    public String getFilePath()
    {
        return filePath;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public FileType getType()
    {
        return type;
    }

    public void setType(FileType type)
    {
        this.type = type;
    }

    @Override
    public String toString() {
        String typeString;
        switch (type) {
            case DIRECTORY : typeString = "DIRECTORY"; break;
            case MOVIE : typeString = "MOVIE"; break;
            case MUSIC : typeString = "MUSIC"; break;
            case IMAGE : typeString = "IMAGE"; break;
            default: typeString = "UNKNOWN"; break;
        }
        return "RemoteFileModel: type=[" + typeString + "], name=[" + fileName + "], path=[" + filePath + "]";
    }
}
