package ged.mediaplayerremote.presentation.model.mapper;

import ged.mediaplayerremote.domain.model.RemoteFile;
import ged.mediaplayerremote.presentation.model.RemoteFileModel;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapper class used to transform {@link RemoteFile} (in the domain layer) to {@link RemoteFileModel} in the
 * presentation layer and vice versa.
 */
@Singleton
public class RemoteFileModelMapper {

    @Inject
    public RemoteFileModelMapper() {
    }

    /**
     * Transform a {@link RemoteFile} into a {@link RemoteFileModel}.
     *
     * @param remoteFile Object to be transformed.
     * @return {@link RemoteFileModel} given valid {@link RemoteFile} otherwise null.
     */
    public RemoteFileModel transform(RemoteFile remoteFile) {
        RemoteFileModel remoteFileModel = null;
        if (remoteFile != null) {
            remoteFileModel = new RemoteFileModel(remoteFile.getFilePath());
            remoteFileModel.setFileName(remoteFile.getFileName());
            remoteFileModel.setType(remoteFile.getType());
        }
        return remoteFileModel;
    }

    /**
     * Transform a {@link RemoteFileModel} into a {@link RemoteFile}.
     *
     * @param remoteFileModel Object to be transformed.
     * @return {@link RemoteFile} given valid {@link RemoteFileModel} otherwise null.
     */
    public RemoteFile transform(RemoteFileModel remoteFileModel) {
        RemoteFile remoteFile = null;
        if (remoteFileModel != null) {
            remoteFile = new RemoteFile(remoteFileModel.getFilePath());
            remoteFile.setFileName(remoteFileModel.getFileName());
            remoteFile.setType(remoteFileModel.getType());
        }
        return remoteFile;
    }

    /**
     * Transform a list of {@link RemoteFile} into a list of {@link RemoteFileModel}.
     *
     * @param remoteFiles List of objects to be transformed.
     * @return A list of {@link RemoteFileModel} given valid list of {@link RemoteFile} otherwise null.
     */
    public List<RemoteFileModel> transform(List<RemoteFile> remoteFiles) {
        List<RemoteFileModel> remoteFileModelList = null;
        if (remoteFiles != null) {
            remoteFileModelList = new ArrayList<>(remoteFiles.size());
            RemoteFileModel remoteFileModel;
            for (RemoteFile remoteFile : remoteFiles) {
                remoteFileModel = transform(remoteFile);
                if (remoteFileModel != null) {
                    remoteFileModelList.add(remoteFileModel);
                }
            }
        }

        return remoteFileModelList;
    }
}
