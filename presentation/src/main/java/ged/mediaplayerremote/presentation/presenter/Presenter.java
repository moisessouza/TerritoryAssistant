package ged.mediaplayerremote.presentation.presenter;

/**
 * Interface representing a Presenter in a Model-View-Presenter design pattern.
 */
public interface Presenter {

    /**
     * A method that should be called in View's onResume() method.
     */
    void resume();

    /**
     * A method that should be called in View's onPause() method.
     */
    void pause();

    /**
     * A method that should be called in View's onDestroy() method.
     */
    void destroy();
}
