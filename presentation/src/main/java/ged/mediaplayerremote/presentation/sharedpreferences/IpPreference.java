package ged.mediaplayerremote.presentation.sharedpreferences;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.util.Patterns;

import java.util.regex.Matcher;

/**
 * A {@link ValidatedEditTextPreference} implementation that only accepts valid IP as an input.
 */
public class IpPreference extends ValidatedEditTextPreference {

    public IpPreference(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextPreferenceStyle);
    }

    public IpPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected boolean isValid(CharSequence input) {
        Matcher matcher = Patterns.IP_ADDRESS.matcher(input);
        return matcher.matches();
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);

        mEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mEditText.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
    }
}
