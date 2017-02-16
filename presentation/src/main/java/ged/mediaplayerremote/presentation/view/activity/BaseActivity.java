package ged.mediaplayerremote.presentation.view.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import ged.mediaplayerremote.AndroidApplication;
import ged.mediaplayerremote.domain.controller.PlaybackStatusProxy;
import ged.mediaplayerremote.domain.repository.UserPreferencesRepository;
import ged.mediaplayerremote.presentation.dagger2.PerFragment;
import ged.mediaplayerremote.presentation.dagger2.components.ApplicationComponent;
import ged.mediaplayerremote.presentation.navigation.Navigator;
import ged.mediaplayerremote.presentation.view.widget.WidgetProvider;

import javax.inject.Inject;

/**
 * Base activity class to be extended by every Activity in this app.
 */
@PerFragment
public abstract class BaseActivity extends AppCompatActivity {

    @Inject PlaybackStatusProxy playbackStatusProxy;
    @Inject Navigator navigator;
    @Inject UserPreferencesRepository userPreferencesRepository;
    @Inject WidgetProvider widgetProvider;

    private AndroidApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplicationComponent().inject(this);
        application = (AndroidApplication) getApplication();
    }

    @Override
    protected void onResume() {
        super.onResume();
        application.activityResumed();
        adjustBrightness();

        if (playbackStatusProxy.isDisconnected()) {
            playbackStatusProxy.connectToServer();
        }

        if (userPreferencesRepository.showWidget()) {
            widgetProvider.enableWidget();
            widgetProvider.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        application.activityPaused();
        if (userPreferencesRepository.showWidget()) {
            widgetProvider.onPause();
        }
    }

    /**
     * Set brightness to minimum, if option has been checked in settings.
     */
    public void adjustBrightness() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        if (userPreferencesRepository.adjustScreenBrightness()) {
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF;
        } else {
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        }
        getWindow().setAttributes(lp);
    }

    /**
     * Add a {@link Fragment} to the base activity layout.
     *
     * @param viewId The host view of the fragment.
     * @param fragment The fragment to be added.
     */
    protected void addFragment(int viewId, Fragment fragment) {
        FragmentTransaction fragmentTransaction = this.getFragmentManager().beginTransaction();
        fragmentTransaction.add(viewId, fragment);
        fragmentTransaction.commit();
    }

    /**
     * Get the Dagger main application component.
     */
    protected ApplicationComponent getApplicationComponent() {
        return ((AndroidApplication) getApplication()).getApplicationComponent();
    }
}
