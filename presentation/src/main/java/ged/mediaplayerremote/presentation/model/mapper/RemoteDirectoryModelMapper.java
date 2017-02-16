package ged.mediaplayerremote.presentation.model.mapper;


import ged.mediaplayerremote.domain.model.RemoteDirectory;
import ged.mediaplayerremote.domain.model.RemoteFile;
import ged.mediaplayerremote.presentation.model.RemoteDirectoryModel;
import ged.mediaplayerremote.presentation.model.RemoteFileModel;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * Mapper class used to transform {@link RemoteDirectory} (in the domain layer) to {@link RemoteDirectoryModel} in the
 * presentation layer.
 */
@Singleton
public class RemoteDirectoryModelMapper {
    private RemoteFileModelMapper mRemoteFileMapper;

    @Inject
    public RemoteDirectoryModelMapper(RemoteFileModelMapper mRemoteFileMapper) {
        this.mRemoteFileMapper = mRemoteFileMapper;
    }

    /**
     * Transform a {@link RemoteDirectory} into a {@link RemoteDirectoryModel}.
     *
     * @param remoteDirectory Object to be transformed.
     * @return {@link RemoteDirectoryModel} given valid {@link RemoteDirectory} otherwise null.
     */
    public RemoteDirectoryModel transform(RemoteDirectory remoteDirectory) {
        RemoteDirectoryModel remoteDirectoryModel = null;
        if (remoteDirectory != null) {
            remoteDirectoryModel = new RemoteDirectoryModel(remoteDirectory.getLocation());

            List<RemoteFile> remoteFileList = remoteDirectory.getFiles();
            List<RemoteFileModel> remoteFileModelList = mRemoteFileMapper.transform(remoteFileList);
            remoteDirectoryModel.setFiles(remoteFileModelList);
        }

        return remoteDirectoryModel;

    }
}
