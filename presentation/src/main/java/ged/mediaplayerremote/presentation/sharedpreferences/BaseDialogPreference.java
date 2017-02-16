package ged.mediaplayerremote.presentation.sharedpreferences;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Window;
import android.widget.TextView;
import ged.mediaplayerremote.R;

/**
 * A {@link DialogPreference} with customizable buttons color.
 */
public class BaseDialogPreference extends DialogPreference {

    private final int buttonColor;

    protected Resources resources;
    protected TextView okButton;
    protected TextView cancelButton;


    public BaseDialogPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        resources = context.getResources();
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseDialogPreference);
        buttonColor = typedArray.getColor(R.styleable.BaseDialogPreference_buttonColor, Integer.MAX_VALUE);
        typedArray.recycle();
    }

    public BaseDialogPreference(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.dialogPreferenceStyle);
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);
        Window window = getDialog().getWindow();
        okButton = (TextView) window.findViewById(resources.getIdentifier("button1", "id", "android"));
        cancelButton = (TextView) window.findViewById(resources.getIdentifier("button2", "id", "android"));
        if (buttonColor != Integer.MAX_VALUE) {
            okButton.setTextColor(buttonColor);
            cancelButton.setTextColor(buttonColor);
        }
    }
}
