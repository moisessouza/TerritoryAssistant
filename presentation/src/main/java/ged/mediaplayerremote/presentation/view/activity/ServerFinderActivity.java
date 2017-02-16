package ged.mediaplayerremote.presentation.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import ged.mediaplayerremote.R;
import ged.mediaplayerremote.presentation.dagger2.HasComponent;
import ged.mediaplayerremote.presentation.dagger2.components.DaggerScopedComponent;
import ged.mediaplayerremote.presentation.dagger2.components.ScopedComponent;
import ged.mediaplayerremote.presentation.view.fragment.ServerFinderFragment;

/**
 * Activity that shows a server finder.
 */
public class ServerFinderActivity extends BaseActivity implements HasComponent<ScopedComponent>,
        ServerFinderFragment.ServerListener {

    private ScopedComponent scopedComponent;

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, ServerFinderActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity_layout);
        initializeActivity();
        initializeInjector();
    }

    private void initializeActivity() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.visible_pref_server_finder));
        }

        addFragment(R.id.fragmentContainer, new ServerFinderFragment());

    }

    private void initializeInjector() {
        scopedComponent = DaggerScopedComponent.builder().applicationComponent(getApplicationComponent()).build();
    }

    @Override
    public ScopedComponent getComponent() {
        return scopedComponent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void onServerChosen(String ip) {
        Intent data = new Intent();
        data.putExtra("SERVER_IP", ip);
        if (getParent() == null) {
            setResult(Activity.RESULT_OK, data);
        } else {
            getParent().setResult(Activity.RESULT_OK, data);
        }
        finish();
    }
}

