package ged.mediaplayerremote.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import ged.mediaplayerremote.R;
import ged.mediaplayerremote.presentation.view.fragment.SettingsFragment;

/**
 * Activity that shows settings.
 */
public class SettingsActivity extends BaseActivity implements SettingsFragment.ServerFinderListener {
    private SettingsFragment settingsFragment;

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.base_activity_layout);
        setResult(RESULT_FIRST_USER);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.settings));
        }

        if (savedInstanceState == null) {
            settingsFragment = new SettingsFragment();
            addFragment(R.id.fragmentContainer, settingsFragment);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            settingsFragment.onUpButton();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        settingsFragment.onUpButton();
    }

    @Override
    public void onServerFinderClicked() {
        navigator.navigateToServerFinder(settingsFragment);
    }
}
