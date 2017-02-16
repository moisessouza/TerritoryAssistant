package ged.mediaplayerremote.data.entity.mapper;

import ged.mediaplayerremote.data.entity.RemoteFileEntity;
import ged.mediaplayerremote.domain.model.RemoteFile;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapper class used to transform {@link RemoteFileEntity} (in the data layer) to {@link RemoteFile} in the
 * domain layer and vice versa.
 */
@Singleton
public class RemoteFileEntityDataMapper {

    @Inject
    public RemoteFileEntityDataMapper() {
    }

    /**
     * Transform a {@link RemoteFileEntity} into an {@link RemoteFile}.
     *
     * @param remoteFileEntity Object to be transformed.
     * @return {@link RemoteFile} given valid {@link RemoteFileEntity} otherwise null.
     */
    public RemoteFile transform(RemoteFileEntity remoteFileEntity) {
        RemoteFile remoteFile = null;
        if (remoteFileEntity != null) {
            remoteFile = new RemoteFile(remoteFileEntity.getFilePath());
            remoteFile.setFileName(remoteFileEntity.getFileName());
            remoteFile.setType(remoteFileEntity.getType());
        }
        return remoteFile;
    }

    /**
     * Transform a {@link RemoteFile} into an {@link RemoteFileEntity}.
     *
     * @param remoteFile Object to be transformed.
     * @return {@link RemoteFileEntity} given valid {@link RemoteFile} otherwise null.
     */
    public RemoteFileEntity transform(RemoteFile remoteFile) {
        RemoteFileEntity remoteFileEntity = null;
        if (remoteFile != null) {
            remoteFileEntity = new RemoteFileEntity(remoteFile.getFilePath());
            remoteFileEntity.setFileName(remoteFile.getFileName());
            remoteFileEntity.setType(remoteFile.getType());
        }
        return remoteFileEntity;
    }

    /**
     * Transform a list of {@link RemoteFileEntity} into a list of {@link RemoteFile}.
     *
     * @param remoteFileEntities Object to be transformed.
     * @return A list of {@link RemoteFile} given valid list of {@link RemoteFileEntity} otherwise null.
     */
    public List<RemoteFile> transformRemoteFileEntities(List<RemoteFileEntity> remoteFileEntities) {
        List<RemoteFile> remoteFileList = null;
        if (remoteFileEntities != null) {
            remoteFileList = new ArrayList<>(remoteFileEntities.size());
            RemoteFile remoteFile;
            for (RemoteFileEntity remoteFileEntity : remoteFileEntities) {
                remoteFile = transform(remoteFileEntity);
                if (remoteFile != null) {
                    remoteFileList.add(remoteFile);
                }
            }
        }

        return remoteFileList;
    }

    /**
     * Transform a list of {@link RemoteFile} into a list of {@link RemoteFileEntity}.
     *
     * @param remoteFiles List of objects to be transformed.
     * @return A list of {@link RemoteFileEntity} given valid list of {@link RemoteFile} otherwise null.
     */
    public List<RemoteFileEntity> transformRemoteFiles(List<RemoteFile> remoteFiles) {
        List<RemoteFileEntity> remoteFileEntityList = null;
        if (remoteFiles != null) {
            remoteFileEntityList = new ArrayList<>(remoteFiles.size());
            RemoteFileEntity remoteFileEntity;
            for (RemoteFile remoteFile : remoteFiles) {
                remoteFileEntity = transform(remoteFile);
                if (remoteFileEntity != null) {
                    remoteFileEntityList.add(remoteFileEntity);
                }
            }
        }

        return remoteFileEntityList;
    }
}
