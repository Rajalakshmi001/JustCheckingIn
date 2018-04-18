package edu.rosehulman.scottae.justcheckingin.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import edu.rosehulman.scottae.justcheckingin.R;

public class NumberPickerPreference extends DialogPreference {

    public static final int DEFAULT_MAX_VALUE = 100;
    public static final int DEFAULT_MIN_VALUE = 0;
    public static final boolean DEFAULT_WRAP_SELECTOR_WHEEL = true;

    private int minValue;
    private int maxValue;
    private boolean wrapSelectorWheel;

    private NumberPicker picker;
    private int value;

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.dialogPreferenceStyle);
    }

    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NumberPickerPreference);
        minValue = a.getInteger(R.styleable.NumberPickerPreference_minValue, DEFAULT_MIN_VALUE);
        maxValue = a.getInteger(R.styleable.NumberPickerPreference_maxValue, DEFAULT_MAX_VALUE);
        wrapSelectorWheel = a.getBoolean(R.styleable.NumberPickerPreference_wrapSelectorWheel, DEFAULT_WRAP_SELECTOR_WHEEL);
        a.recycle();
    }

    @Override
    protected View onCreateDialogView() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;

        picker = new NumberPicker(getContext());
        picker.setLayoutParams(layoutParams);

        FrameLayout dialogView = new FrameLayout(getContext());
        dialogView.addView(picker);

        return dialogView;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        picker.setMinValue(minValue);
        picker.setMaxValue(maxValue);
        picker.setWrapSelectorWheel(wrapSelectorWheel);
        picker.setValue(getValue());
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

    public void setValue(int value) {
        this.value = value;
        persistInt(this.value);
    }

    public int getValue() {
        return this.value;
    }
//    public NumberPickerPreference(Context context, AttributeSet attrs) {
//        super(context, attrs);
//
//        setDialogLayoutResource(R.layout.number_picker_dialog);
//        setPositiveButtonText(android.R.string.ok);
//        setNegativeButtonText(android.R.string.cancel);
//
//        setDialogIcon(null);
//    }
//    @Override
//    protected void onDialogClosed(boolean positiveResult) {
//        // When the user selects "OK", persist the new value
//        if (positiveResult) {
//            persistInt(mNewValue);
//        }
//    }
//
//    @Override
//    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
//        if (restorePersistedValue) {
//            // Restore existing state
//            mCurrentValue = this.getPersistedInt(0);
//        } else {
//            // Set default state from the XML attribute
//            mCurrentValue = (Integer) defaultValue;
//            persistInt(mCurrentValue);
//        }
//    }
//    @Override
//    protected Object onGetDefaultValue(TypedArray a, int index) {
//        return a.getInteger(index, 0);
//    }
}
