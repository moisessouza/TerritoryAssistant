package ged.mediaplayerremote.presentation.sharedpreferences;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.AttributeSet;

/**
 * A {@link ValidatedEditTextPreference} implementation that only accepts valid network port numbers as an input.
 */
public class PortPreference extends ValidatedEditTextPreference {

    public PortPreference(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextPreferenceStyle);
    }

    public PortPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected boolean isValid(CharSequence input) {
        int port;
        try {
            port = Integer.parseInt(input.toString());
        } catch (NumberFormatException ex) {
            return false;
        }

        return port > 0 && port < 65535;
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);
        mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
    }
}
