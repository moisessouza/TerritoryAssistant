package ged.mediaplayerremote.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import ged.mediaplayerremote.R;
import ged.mediaplayerremote.presentation.dagger2.HasComponent;
import ged.mediaplayerremote.presentation.dagger2.components.DaggerScopedComponent;
import ged.mediaplayerremote.presentation.dagger2.components.ScopedComponent;
import ged.mediaplayerremote.presentation.view.fragment.FileExplorerFragment;

/**
 * Activity that shows a file explorer.
 */
public class FileExplorerActivity extends BaseActivity implements HasComponent<ScopedComponent> {

    private ScopedComponent component;

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, FileExplorerActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.base_activity_layout);

        initializeActivity();
        initializeInjector();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public ScopedComponent getComponent() {
        return component;
    }

    private void initializeActivity() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.file_manager));
        }
        FileExplorerFragment fileExplorerFragment = new FileExplorerFragment();
        addFragment(R.id.fragmentContainer, fileExplorerFragment);
    }

    private void initializeInjector() {
        component = DaggerScopedComponent.builder().applicationComponent(getApplicationComponent()).build();
    }
}
