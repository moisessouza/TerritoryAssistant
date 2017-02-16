package ged.mediaplayerremote.data.parser;


import ged.mediaplayerremote.data.entity.PlaybackStatusEntity;
import ged.mediaplayerremote.data.entity.RemoteDirectoryEntity;
import ged.mediaplayerremote.data.entity.RemoteFileEntity;
import ged.mediaplayerremote.domain.model.FileType;
import ged.mediaplayerremote.domain.model.PlaybackState;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import javax.inject.Inject;
import java.util.*;

/**
 * Parser class used to transform data from raw HTML documents retrieved from MPC-HC Server into data objects used by
 * the app.
 */
public class HtmlParser {

    private final String[] movieExtensions = {".avi", ".mkv", ".mp4"};
    private final String[] imageExtensions = {".jpg", ".bmp", ".png"};
    private final String[] musicExtensions = {".mp3", ".wav", ".flac", ".aac"};

    @Inject
    public HtmlParser() { }

    /**
     * Parse raw HTML document to {@link PlaybackStatusEntity}.
     *
     * @param document A /variables.html page retrieved from MPC-HC server.
     * @return {@link PlaybackStatusEntity}.
     */
    public PlaybackStatusEntity htmlToPlaybackStatusEntity(Document document) {

        PlaybackStatusEntity playbackStatus = new PlaybackStatusEntity();

        String filename = document.getElementById("file").ownText();
        playbackStatus.setFileName(filename);

        int playbackState = Integer.parseInt(document.getElementById("state").ownText());
        switch (playbackState) {
            case -1 : playbackStatus.setPlaybackState(PlaybackState.NO_FILE_SELECTED); break;
            case 0 : playbackStatus.setPlaybackState(PlaybackState.STOPPED); break;
            case 1 : playbackStatus.setPlaybackState(PlaybackState.PAUSED); break;
            case 2 : playbackStatus.setPlaybackState(PlaybackState.PLAYING); break;
        }

        int volume = Integer.parseInt(document.getElementById("volumelevel").ownText());
        playbackStatus.setVolume(volume);

        long position = Long.parseLong(document.getElementById("position").ownText());
        playbackStatus.setPosition(position);

        String positionString = document.getElementById("positionstring").ownText();
        playbackStatus.setPositionString(positionString);

        long duration = Long.parseLong(document.getElementById("duration").ownText());
        playbackStatus.setDuration(duration);

        String durationString = document.getElementById("durationstring").ownText();
        playbackStatus.setDurationString(durationString);

        String mutedValue = document.getElementById("muted").ownText();
        if (mutedValue.equals("1")) {
            playbackStatus.setMuted(true);
        } else {
            playbackStatus.setMuted(false);
        }

        playbackStatus.setTimeStamp(System.currentTimeMillis());

        return playbackStatus;
    }

    /**
     * Parse raw HTML document to {@link RemoteDirectoryEntity}.
     *
     * @param document A /browser.html page retrieved from MPC-HC server.
     * @return {@link RemoteDirectoryEntity}.
     */
    public RemoteDirectoryEntity htmlToFileEntities(Document document) {

        Elements tableRows = document.body().getElementsByTag("tr");
        ArrayList<RemoteFileEntity> fileEntities = new ArrayList<>(tableRows.size());
        String location = tableRows.get(0).childNode(1).childNode(2).toString();
        RemoteDirectoryEntity remoteDirectoryEntity = new RemoteDirectoryEntity(location);

        int index = 2;
        while (index < tableRows.size()) {
            RemoteFileEntity remoteFileEntity;
            Element tableRow = tableRows.get(index);

            Node node = tableRow.childNode(1).childNode(0);
            String filePath = node.attr("href");
            remoteFileEntity = new RemoteFileEntity(filePath);

            node =  node.childNode(0);
            String fileName = node.toString();
            remoteFileEntity.setFileName(fileName);
            if (tableRow.children().get(0).hasClass("dirname")) {
                remoteFileEntity.setType(FileType.DIRECTORY);
            } else {

                remoteFileEntity.setType(getFileType(fileName));
            }
            fileEntities.add(remoteFileEntity);
            index++;
        }
        remoteDirectoryEntity.setFiles(fileEntities);
        return remoteDirectoryEntity;
    }

    private FileType getFileType(String fileName)
    {
        fileName = fileName.toLowerCase();

        for (String movieType : movieExtensions) {
            if (fileName.endsWith(movieType)) return FileType.MOVIE;
        }
        for (String imageType : imageExtensions) {
            if (fileName.endsWith(imageType)) return FileType.IMAGE;
        }
        for (String musicType : musicExtensions) {
            if (fileName.endsWith(musicType)) return FileType.MUSIC;
        }

        return FileType.UNKNOWN;
    }
}
