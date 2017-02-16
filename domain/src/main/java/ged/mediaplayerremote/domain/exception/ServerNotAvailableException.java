package ged.mediaplayerremote.domain.exception;

/**
 * Exception throw by the application when a command is issued, but there is no connection to server.
 */
public class ServerNotAvailableException extends RuntimeException {

    public ServerNotAvailableException() {
        super();
    }

    public ServerNotAvailableException(String s) {
        super(s);
    }

    public ServerNotAvailableException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ServerNotAvailableException(Throwable throwable) {
        super(throwable);
    }

}
