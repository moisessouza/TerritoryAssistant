package ged.mediaplayerremote.presentation.sharedpreferences;

import android.content.Context;
import android.util.AttributeSet;

/**
 * A {@link BaseDialogPreference} that performs factory reset of the app.
 */
public class ResetPreference extends BaseDialogPreference {

    public ResetPreference(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.dialogPreferenceStyle);
    }

    public ResetPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            resetAllSettings();
            callChangeListener(true);
        }
    }

    private void resetAllSettings() {
        getSharedPreferences().edit().clear().apply();
    }
}
