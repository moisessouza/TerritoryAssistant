package ged.mediaplayerremote.presentation.sharedpreferences;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.AttributeSet;

/**
 * A {@link ValidatedEditTextPreference} implementation that only accepts integers higher than 0.
 */
public class ConnectionTimeoutPreference extends ValidatedEditTextPreference {

    public ConnectionTimeoutPreference(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextPreferenceStyle);
    }

    public ConnectionTimeoutPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected boolean isValid(CharSequence input) {
        int timeout;
        try {
            timeout = Integer.parseInt(input.toString());
        } catch (NumberFormatException ex) {
            return false;
        }

        return timeout > 0;
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);
        mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
    }
}
