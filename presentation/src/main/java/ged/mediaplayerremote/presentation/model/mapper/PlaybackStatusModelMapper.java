package ged.mediaplayerremote.presentation.model.mapper;


import ged.mediaplayerremote.domain.model.PlaybackStatus;
import ged.mediaplayerremote.presentation.model.PlaybackStatusModel;

import javax.inject.Inject;

/**
 * Mapper class used to transform {@link PlaybackStatus} from the domain layer to {@link PlaybackStatusModel} in the
 * presentation layer.
 */
public class PlaybackStatusModelMapper {

    @Inject
    public PlaybackStatusModelMapper() {
    }

    /**
     * Transform a {@link PlaybackStatus} into an {@link PlaybackStatusModel}.
     *
     * @param playbackStatus Object to be transformed.
     * @return {@link PlaybackStatusModel} given valid {@link PlaybackStatus} otherwise null.
     */
    public PlaybackStatusModel transform(PlaybackStatus playbackStatus) {
        PlaybackStatusModel playbackStatusModel = null;
        if (playbackStatus != null) {
            playbackStatusModel = new PlaybackStatusModel();
            playbackStatusModel.setFileName(playbackStatus.getFileName());
            playbackStatusModel.setPlaybackState(playbackStatus.getPlaybackState());
            playbackStatusModel.setVolume(playbackStatus.getVolume());
            playbackStatusModel.setPosition(playbackStatus.getPosition());
            playbackStatusModel.setPositionString(playbackStatus.getPositionString());
            playbackStatusModel.setDuration(playbackStatus.getDuration());
            playbackStatusModel.setDurationString(playbackStatus.getDurationString());
            playbackStatusModel.setMuted(playbackStatus.isMuted());
            playbackStatusModel.setTimeStamp(playbackStatus.getTimeStamp());
        }

        return playbackStatusModel;
    }
}
