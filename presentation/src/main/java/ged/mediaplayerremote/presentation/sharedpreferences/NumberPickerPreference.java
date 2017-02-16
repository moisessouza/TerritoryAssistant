package ged.mediaplayerremote.presentation.sharedpreferences;


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import ged.mediaplayerremote.R;


/**
 * A {@link android.preference.Preference} that displays a number picker as a dialog.
 */
public class NumberPickerPreference extends DialogPreference {

    private static final int DEFAULT_MAX_VALUE = 100;
    private static final int DEFAULT_MIN_VALUE = 0;
    private static final boolean DEFAULT_WRAP_SELECTOR_WHEEL = true;
    private static final boolean DEFAULT_FOCUSABLE = false;

    private final int minValue;
    private final int maxValue;
    private final boolean wrapSelectorWheel;
    private final boolean focusable;
    private final String unit;
    private final String message;

    private NumberPicker picker;
    private TextView messageTextView;
    private TextView unitTextView;
    private TextView unitTextViewDuplicate; // A hax to keep numberPicker in the center of layout

    private int value;

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.dialogPreferenceStyle);
    }

    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NumberPickerPreference);
        minValue = a.getInteger(R.styleable.NumberPickerPreference_minValue, DEFAULT_MIN_VALUE);
        maxValue = a.getInteger(R.styleable.NumberPickerPreference_maxValue, DEFAULT_MAX_VALUE);
        unit = a.getString(R.styleable.NumberPickerPreference_unit);
        message = a.getString(R.styleable.NumberPickerPreference_message);
        wrapSelectorWheel = a.getBoolean(R.styleable.NumberPickerPreference_wrapSelectorWheel, DEFAULT_WRAP_SELECTOR_WHEEL);
        focusable = a.getBoolean(R.styleable.NumberPickerPreference_focusable, DEFAULT_FOCUSABLE);
        a.recycle();
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
        persistInt(this.value);
    }

    public String getUnit() {
        return this.unit;
    }

    @Override
    protected View onCreateDialogView() {

        FrameLayout dialogContainer = new FrameLayout(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View layout = inflater.inflate(R.layout.preference_number_picker, dialogContainer, false);

        this.picker = (NumberPicker) layout.findViewById(R.id.numberPicker);
        this.unitTextView = (TextView) layout.findViewById(R.id.unit);
        this.unitTextViewDuplicate = (TextView) layout.findViewById(R.id.unitInvisibleDuplicate);
        this.messageTextView = (TextView) layout.findViewById(R.id.message);
        if (!focusable) {
            picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        }

        dialogContainer.addView(layout);
        return dialogContainer;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        picker.setMinValue(minValue);
        picker.setMaxValue(maxValue);
        picker.setWrapSelectorWheel(wrapSelectorWheel);
        picker.setValue(getValue());
        unitTextView.setText(unit);
        unitTextViewDuplicate.setText(unit);
        messageTextView.setText(message);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            picker.clearFocus();
            int newValue = picker.getValue();
            if (callChangeListener(newValue)) {
                setValue(newValue);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, minValue);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setValue(restorePersistedValue ? getPersistedInt(minValue) : (Integer) defaultValue);
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);
        final Resources res = getContext().getResources();
        final Window window = getDialog().getWindow();

        TextView button1 = (TextView) window.findViewById(res.getIdentifier("button1", "id", "android"));
        button1.setTextColor(ContextCompat.getColor(getContext(), R.color.accent100));
        TextView button2 = (TextView) window.findViewById(res.getIdentifier("button2", "id", "android"));
        button2.setTextColor(ContextCompat.getColor(getContext(), R.color.accent100));

        setDividerColor(ContextCompat.getColor(getContext(), R.color.accent100));
    }

    /**
     * A reflection hack to change dividers color to an accent color without rewriting NumberPickerPreference class from
     * scratch.
     */
    private void setDividerColor(int color) {
        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(picker, colorDrawable);
                } catch (Throwable ignored) {
                }
                break;
            }
        }
    }
}