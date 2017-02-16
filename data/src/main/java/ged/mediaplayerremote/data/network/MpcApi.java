package ged.mediaplayerremote.data.network;

import android.content.res.Resources;
import ged.mediaplayerremote.data.R;
import ged.mediaplayerremote.data.entity.PlaybackStatusEntity;
import ged.mediaplayerremote.data.entity.RemoteDirectoryEntity;
import ged.mediaplayerremote.data.entity.ServerEntity;
import ged.mediaplayerremote.data.parser.HtmlParser;
import ged.mediaplayerremote.domain.model.PlaybackStatus;
import ged.mediaplayerremote.domain.model.RemoteDirectory;
import ged.mediaplayerremote.domain.repository.UserPreferencesRepository;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import rx.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Map;

/**
 * Api Connection class, based on {@link Jsoup} used to retrieve data from the MPC-HC server and send commands to the player.
 * Results are returned asynchronously as {@link Observable} objects.
 */
@Singleton
public class MpcApi {
    private UserPreferencesRepository userPreferences;
    private HtmlParser htmlParser;
    private Resources resources;


    @Inject
    public MpcApi(UserPreferencesRepository userPreferences, HtmlParser htmlParser, Resources resources) {
        this.userPreferences = userPreferences;
        this.htmlParser = htmlParser;
        this.resources = resources;
    }

    /**
     * Send a command to the MPC-HC server. Response will be emitted through an {@link Observable}
     *
     * @param serverEntity which server should the command be sent to.
     * @param command a {@link Map} collection that should contain one or more {@link String} pairs. Key of the first
     *                pair should equal "wm_command" with desired command code as value. Some commands require
     *                additional parameters such as "position" or "volume".
     */
    public Observable<Void> sendRequest(ServerEntity serverEntity, Map<String, String> command) {
        return Observable.create(subscriber ->
        {
            if (serverEntity == null)
                subscriber.onCompleted();
            else {
                String ip = serverEntity.getIp();
                String port = serverEntity.getPort();

                String url = "http://" + ip + ":" + port + "/command.html";

                Connection connection = Jsoup.connect(url);
                connection.timeout(serverEntity.getConnectionTimeout());
                connection.data(command);
                try {
                    connection.post();
                } catch (IOException ex) {

                    subscriber.onError(new IOException(resources.getString(R.string.error_connection_timeout), ex));
                }
                subscriber.onCompleted();
            }

        });

    }

    /**
     * Get an {@link Observable} which will emit a {@link PlaybackStatus} object every second. New objects will be
     * emitted until unsubscribed. If an update could not be retrieved, null will be emitted.
     *
     *
     * @param serverEntity which server should the status be retrieved from.
     * @return {@link Observable} object which will emit {@link PlaybackStatus} every second when subscribed to.
     */
    public Observable<PlaybackStatusEntity> getPlaybackStatusUpdates(ServerEntity serverEntity) {

        return Observable.create(subscriber ->
        {
            while (!subscriber.isUnsubscribed()) {
                try {
                    long connectionStart = System.nanoTime();
                    PlaybackStatusEntity playbackStatusEntity = getStatusFromServer(serverEntity);
                    long connectionEnd = System.nanoTime();
                    subscriber.onNext(playbackStatusEntity);
                    int responseMilliseconds = (int) ((connectionEnd - connectionStart) / 1000000);
                    int sleepTime = 1000 - responseMilliseconds;
                    if (sleepTime > 1) {
                        Thread.sleep(sleepTime);
                    }

                } catch (IOException e) {
                    subscriber.onNext(null);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Get an {@link Observable} which will emit image previews from the server, in the form of byte array. New image
     * will be emitted continuously until unsubscribed. If an image could not be retrieved, null will be emitted. The
     * delay between images depends on user settings, retrieved from {@link UserPreferencesRepository}.
     *
     * @param serverEntity which server should the image be retrieved from.
     */
    public Observable<byte[]> getSnapshot(ServerEntity serverEntity) {
        String ip = serverEntity.getIp();

        String port = serverEntity.getPort();
        int timeout = serverEntity.getConnectionTimeout();

        return Observable.create(subscriber ->
        {

            String url = "http://" + ip + ":" + port + "/snapshot.jpg";

            byte[] image = new byte[16384];

            while (true) {
                if (subscriber.isUnsubscribed())
                    break;
                try {
                    Connection connection = Jsoup.connect(url).ignoreContentType(true).timeout(timeout);
                    Connection.Response resultImageResponse = connection.execute();
                    image = resultImageResponse.bodyAsBytes();
                    subscriber.onNext(image);

                } catch (IOException ex) {
                    subscriber.onNext(null);
                }
                int sleep = userPreferences.getSnapshotUpdateInterval() * 1000;

                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Get an {@link Observable} which will emit a {@link RemoteDirectory}
     * @param serverEntity which server should the directory be retrieved from.
     * @param location location of the directory.
     */
    public Observable<RemoteDirectoryEntity> getDirectory(ServerEntity serverEntity, String location) {
        return Observable.create(subscriber ->
        {

            String ip = serverEntity.getIp();
            String port = serverEntity.getPort();
            String url;

            if (location == null)
                url = "http://" + ip + ":" + port + "/browser.html?";
            else
                url = "http://" + ip + ":" + port + location;

            Connection connection = Jsoup.connect(url);
            connection.timeout(serverEntity.getConnectionTimeout());

            Document document = null;
            try {
                document = connection.get();
            } catch (IOException e) {
                subscriber.onError(new IOException(resources.getString(R.string.error_connection_timeout), e));
            }


            RemoteDirectoryEntity remoteDirectoryEntity = htmlParser.htmlToFileEntities(document);

            subscriber.onNext(remoteDirectoryEntity);
            subscriber.onCompleted();
        });
    }

    private PlaybackStatusEntity getStatusFromServer(ServerEntity server) throws IOException {
        String port = server.getPort();
        String ip = server.getIp();
        int timeout = server.getConnectionTimeout();

        String url = "http://" + ip + ":" + port + "/variables.html";

        Connection connection = Jsoup.connect(url);
        connection.timeout(timeout);

        PlaybackStatusEntity playbackStatusEntity;

        Connection.Response response = connection.execute();
        Document doc = response.parse();


        playbackStatusEntity = htmlParser.htmlToPlaybackStatusEntity(doc);


        return playbackStatusEntity;
    }
}
