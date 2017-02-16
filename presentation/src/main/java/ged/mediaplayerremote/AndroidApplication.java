package ged.mediaplayerremote;

import android.app.Application;
import ged.mediaplayerremote.domain.controller.PlaybackStatusProxy;
import ged.mediaplayerremote.presentation.dagger2.components.ApplicationComponent;
import ged.mediaplayerremote.presentation.dagger2.components.DaggerApplicationComponent;
import ged.mediaplayerremote.presentation.dagger2.modules.ApplicationModule;
import ged.mediaplayerremote.presentation.dagger2.modules.WidgetInteractorModule;

/**
 * Main Android application class.
 */
public class AndroidApplication extends Application implements PlaybackStatusProxy.ApplicationStatus {

    private ApplicationComponent applicationComponent;
    private boolean anyActivityRunning;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeInjector();
        getApplicationComponent().inject(this);

    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    /**
     *  A method to check if any activity is currently resumed. Note: this method returns false if one activity is
     *  paused and another one is just starting. To ensure all activities are in background, this method should be
     *  called more than once with a 100ms or more interval between calls.
     */
    @Override
    public boolean isAnyActivityVisible() {
        return anyActivityRunning;
    }

    /**
     * A method for activities to call, when they are resumed.
     */
    public void activityResumed() {
        anyActivityRunning = true;

    }

    /**
     * A method for activities to call, when they are paused.
     */
    public void activityPaused() {
        anyActivityRunning = false;
    }

    private void initializeInjector() {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .widgetInteractorModule(new WidgetInteractorModule())
                .build();
    }
}
