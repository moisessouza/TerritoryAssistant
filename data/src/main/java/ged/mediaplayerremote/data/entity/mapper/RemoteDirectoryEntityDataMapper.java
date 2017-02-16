package ged.mediaplayerremote.data.entity.mapper;

import ged.mediaplayerremote.data.entity.RemoteFileEntity;
import ged.mediaplayerremote.domain.model.RemoteDirectory;
import ged.mediaplayerremote.data.entity.RemoteDirectoryEntity;
import ged.mediaplayerremote.domain.model.RemoteFile;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * Mapper class used to transform {@link RemoteDirectoryEntity} (in the data layer) to {@link RemoteDirectory} in the
 * domain layer and vice versa.
 */
@Singleton
public class RemoteDirectoryEntityDataMapper {

    private RemoteFileEntityDataMapper mRemoteFileEntityDataMapper;

    @Inject
    public RemoteDirectoryEntityDataMapper(RemoteFileEntityDataMapper mRemoteFileEntityDataMapper) {
        this.mRemoteFileEntityDataMapper = mRemoteFileEntityDataMapper;
    }

    /**
     * Transform a {@link RemoteDirectoryEntity} into a {@link RemoteDirectory}.
     *
     * @param remoteDirectoryEntity Object to be transformed.
     * @return {@link RemoteDirectory} given valid {@link RemoteDirectoryEntity} otherwise null.
     */
    public RemoteDirectory transform(RemoteDirectoryEntity remoteDirectoryEntity) {
        RemoteDirectory remoteDirectory = null;

        if (remoteDirectoryEntity != null) {
            remoteDirectory = new RemoteDirectory(remoteDirectoryEntity.getLocation());

            List<RemoteFileEntity> remoteFileEntityList = remoteDirectoryEntity.getFiles();
            List<RemoteFile> remoteFileList
                    = mRemoteFileEntityDataMapper.transformRemoteFileEntities(remoteFileEntityList);
            remoteDirectory.setFiles(remoteFileList);
        }

        return remoteDirectory;
    }

    /**
     * Transform a {@link RemoteDirectory} into a {@link RemoteDirectoryEntity}.
     *
     * @param remoteDirectory Object to be transformed.
     * @return {@link RemoteDirectoryEntity} given valid {@link RemoteDirectory} otherwise null.
     */
    public RemoteDirectoryEntity transform(RemoteDirectory remoteDirectory) {
        RemoteDirectoryEntity remoteDirectoryEntity = null;

        if (remoteDirectory != null) {
            remoteDirectoryEntity = new RemoteDirectoryEntity(remoteDirectory.getLocation());

            List<RemoteFile> remoteFileList = remoteDirectory.getFiles();
            List<RemoteFileEntity> remoteFileEntityList
                    = mRemoteFileEntityDataMapper.transformRemoteFiles(remoteFileList);
            remoteDirectoryEntity.setFiles(remoteFileEntityList);
        }

        return remoteDirectoryEntity;
    }
}
