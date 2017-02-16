package ged.mediaplayerremote.presentation.sharedpreferences;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import ged.mediaplayerremote.R;

import java.lang.reflect.Field;

/**
 * Extension of {@link EditTextPreference} with added feature of validating user input. If the input is incorrect, the
 * positive button will be disabled, and cursor color will be changed to red.
 */
public abstract class ValidatedEditTextPreference extends EditTextPreference {

    private final boolean mRemoveLeadingZeros;
    private TextView mOkButton;
    private Resources mResources;
    protected EditText mEditText;
    private int colorError;
    private int colorAccent;


    public ValidatedEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mResources = context.getResources();
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ValidatedEditTextPreference);
        mRemoveLeadingZeros = typedArray.getBoolean(R.styleable.ValidatedEditTextPreference_removeLeadingZeros, false);
        typedArray.recycle();

        colorError = ContextCompat.getColor(getContext(), R.color.error);
        colorAccent = ContextCompat.getColor(getContext(), R.color.accent100);
    }

    protected abstract boolean isValid(CharSequence input);

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);

        initializeDialog();

        String text = getText();
        if (isValid(text)) {
            enableOkButton();
        } else {
            disableOkButton();
        }
        mEditText.addTextChangedListener(textWatcher);
    }

    private void initializeDialog() {
        Window window = getDialog().getWindow();
        mOkButton = (TextView) window.findViewById(mResources.getIdentifier("button1", "id", "android"));
        TextView mCancelButton = (TextView) window.findViewById(mResources.getIdentifier("button2", "id", "android"));
        mEditText = getEditText();
        mEditText.setSingleLine();
        mEditText.setSelection(mEditText.length());
        mOkButton.setTextColor(colorAccent);
        mCancelButton.setTextColor(colorAccent);
    }

    private void enableOkButton() {
        mEditText.getBackground().mutate().setColorFilter(colorAccent, PorterDuff.Mode.SRC_ATOP);
        mOkButton.setAlpha(1.0f);
        mOkButton.setClickable(true);
        setCursorColor(colorAccent);


    }

    private void disableOkButton() {
        mEditText.getBackground().mutate().setColorFilter(colorError, PorterDuff.Mode.SRC_ATOP);
        mOkButton.setAlpha(0.5f);
        mOkButton.setClickable(false);
        setCursorColor(colorError);
    }

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (isValid(charSequence)) {
                enableOkButton();
            } else {
                disableOkButton();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (mRemoveLeadingZeros) {
                String text = editable.toString();
                if (text.startsWith("0")) {
                    editable.delete(0, 1);
                }
            }
        }
    };


    /**
     * A reflection hack to change cursor color without rewriting EditTextPreference class from scratch.
     */
    private void setCursorColor(int color) {
        try {
            Field fCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");

            fCursorDrawableRes.setAccessible(true);
            int mCursorDrawableRes = fCursorDrawableRes.getInt(mEditText);
            Field fEditor = TextView.class.getDeclaredField("mEditor");
            fEditor.setAccessible(true);
            Object editor = fEditor.get(mEditText);
            Class<?> clazz = editor.getClass();
            Field fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
            fCursorDrawable.setAccessible(true);

            Drawable[] drawables = new Drawable[2];
            Resources res = mEditText.getContext().getResources();
            drawables[0] = res.getDrawable(mCursorDrawableRes);
            drawables[1] = res.getDrawable(mCursorDrawableRes);
            drawables[0].setColorFilter(color, PorterDuff.Mode.SRC_IN);
            drawables[1].setColorFilter(color, PorterDuff.Mode.SRC_IN);
            fCursorDrawable.set(editor, drawables);
        } catch (final Throwable ignored) {
        }
    }
}
