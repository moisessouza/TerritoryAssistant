package ged.mediaplayerremote.presentation.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import ged.mediaplayerremote.R;
import ged.mediaplayerremote.presentation.dagger2.HasComponent;
import ged.mediaplayerremote.presentation.dagger2.components.DaggerScopedComponent;
import ged.mediaplayerremote.presentation.dagger2.components.ScopedComponent;
import ged.mediaplayerremote.presentation.navigation.Navigator;
import ged.mediaplayerremote.presentation.view.fragment.MainFragment;

/**
 * Main application screen. The first launched activity. Starts with a splash screen and changes into normal theme once
 * all components have been created.
 */
public class MainActivity extends BaseActivity implements HasComponent<ScopedComponent>,
        MainFragment.MainFragmentEventListener
{
    private ScopedComponent component;
    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.MainScreenTheme);
        setContentView(R.layout.base_activity_layout);

        this.initializeInjector();

        if (savedInstanceState == null) {
            mainFragment = new MainFragment();
            addFragment(R.id.fragmentContainer, mainFragment);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        int action = event.getAction();
        int keyCode = event.getKeyCode();
        boolean physicalButtonsEnabled = userPreferencesRepository.usePhysicalVolumeButtons();

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (action == KeyEvent.ACTION_DOWN && mainFragment != null && physicalButtonsEnabled ) {
                mainFragment.onVolumeUpPressed();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (action == KeyEvent.ACTION_DOWN && mainFragment != null && physicalButtonsEnabled) {
                mainFragment.onVolumeDownPressed();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (action == KeyEvent.ACTION_DOWN && mainFragment != null) {
                mainFragment.onBackKeyPressed();
            }
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    @Override
    public ScopedComponent getComponent() {
        return this.component;
    }

    @Override
    public void onMenuItemClicked(MenuItem menuItem) {
        String settings = getString(R.string.settings);
        if (menuItem.getTitle().equals(settings))
            navigator.navigateToSettings(this);

        String fileExplorer = getString(R.string.file_manager);
        if (menuItem.getTitle().equals(fileExplorer))
            navigator.navigateToFileExplorer(this);

        String help = getString(R.string.help);
        if (menuItem.getTitle().equals(help))
            navigator.navigateToHelp(this);
    }

    @Override
    public void closeApplication() {
        moveTaskToBack(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Navigator.SHOULD_PERFORM_FACTORY_RESET_REQUEST_CODE) {
            if (resultCode == Navigator.FACTORY_RESET_DONE_RESULT_CODE) {
                mainFragment.onSettingsReset();
            }
        }
    }

    private void initializeInjector() {
        this.component = DaggerScopedComponent.builder().applicationComponent(getApplicationComponent()).build();
    }
}
