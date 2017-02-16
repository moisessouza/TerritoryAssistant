package ged.mediaplayerremote.data.entity.mapper;

import ged.mediaplayerremote.data.entity.PlaybackStatusEntity;
import ged.mediaplayerremote.domain.model.PlaybackStatus;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Mapper class used to transform {@link PlaybackStatusEntity} from the data layer to {@link PlaybackStatus} in the
 * domain layer.
 */
@Singleton
public class StatusEntityDataMapper {

    @Inject
    public StatusEntityDataMapper() {
    }

    /**
     * Transform a {@link PlaybackStatusEntity} into an {@link PlaybackStatus}.
     *
     * @param playbackStatusEntity Object to be transformed.
     * @return {@link PlaybackStatus} given valid {@link PlaybackStatusEntity} otherwise null.
     */
    public PlaybackStatus transform(PlaybackStatusEntity playbackStatusEntity) {
        PlaybackStatus playbackStatus = null;
        if (playbackStatusEntity != null) {
            playbackStatus = new PlaybackStatus();
            playbackStatus.setFileName(playbackStatusEntity.getFileName());
            playbackStatus.setPlaybackState(playbackStatusEntity.getPlaybackState());
            playbackStatus.setVolume(playbackStatusEntity.getVolume());
            playbackStatus.setPosition(playbackStatusEntity.getPosition());
            playbackStatus.setPositionString(playbackStatusEntity.getPositionString());
            playbackStatus.setDuration(playbackStatusEntity.getDuration());
            playbackStatus.setDurationString(playbackStatusEntity.getDurationString());
            playbackStatus.setMuted(playbackStatusEntity.isMuted());
            playbackStatus.setTimeStamp(playbackStatusEntity.getTimeStamp());
        }

        return playbackStatus;
    }
}
