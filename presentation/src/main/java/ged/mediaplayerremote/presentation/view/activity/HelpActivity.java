package ged.mediaplayerremote.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import ged.mediaplayerremote.R;
import ged.mediaplayerremote.presentation.view.fragment.HelpFragment;

/**
 * Activity that shows a view with a user guide.
 */
public class HelpActivity extends BaseActivity {


    public static Intent getCallingIntent(Context context) {
        return new Intent(context, HelpActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity_layout);
        initializeActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return true;
    }

    private void initializeActivity() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.help));
        }
        HelpFragment helpFragment = new HelpFragment();
        addFragment(R.id.fragmentContainer, helpFragment);
    }
}
