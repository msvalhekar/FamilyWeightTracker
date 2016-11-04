package com.mk.familyweighttracker.Views;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.mk.familyweighttracker.Framework.NumericParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mvalhekar on 31-10-2016.
 */
public class NumericPickerView extends LinearLayout {
    private Context context;

    private NumericParser numericParser;
    private NumberPicker hundredthsPicker;
    private NumberPicker tenthsPicker;
    private NumberPicker onesPicker;
    private NumberPicker tensPicker;
    private NumberPicker hundredsPicker;

    private boolean showHundreds;
    private boolean showTens;
    private boolean showOnes;
    private boolean showTenths;
    private boolean showHundredths;
    private boolean showDecimal;

    public NumericPickerView(Context context) {
        super(context);
        this.context = context;

        initLayout();
        setNumberFormat(true, true, true, true);
    }

    public void setNumberFormat(boolean showHundreds, boolean showTens, boolean showTenths, boolean showHundredths) {
        this.showOnes = true;
        this.showHundreds = false;
        this.showTens = false;
        this.showTenths = false;
        this.showHundredths = false;
        this.showDecimal = false;

        if(showHundreds) {
            this.showHundreds = true;
            this.showTens = true;
        } else if (showTens) {
            this.showTens = true;
        }

        if(showHundredths) {
            this.showHundredths = true;
            this.showTenths = true;
            this.showDecimal = true;
        } else if (showTenths) {
            this.showTenths = true;
            this.showDecimal = true;
        }
        showHidePickers();
    }

    private void showHidePickers() {
        hundredthsPicker.setVisibility(showHundredths ? VISIBLE : GONE);
        tenthsPicker.setVisibility(showTenths ? VISIBLE : GONE);
        onesPicker.setVisibility(showOnes ? VISIBLE : GONE);
        tensPicker.setVisibility(showTens ? VISIBLE : GONE);
        hundredsPicker.setVisibility(showHundreds ? VISIBLE : GONE);
    }

    public void setNumber(double number) {
        numericParser = new NumericParser(number);
        setPickersForNumber();
    }

    public double getNumber() {
        return numericParser.getNumber();
    }

    private void initLayout() {
        setOrientation(LinearLayout.HORIZONTAL);
        setHorizontalGravity(Gravity.CENTER);

        hundredthsPicker = getNumericPickerFor(NumericParser.HUNDREDTHS_PLACE);
        tenthsPicker = getNumericPickerFor(NumericParser.TENTHS_PLACE);
        onesPicker = getNumericPickerFor(NumericParser.ONES_PLACE);
        tensPicker = getNumericPickerFor(NumericParser.TENS_PLACE);
        hundredsPicker = getNumericPickerFor(NumericParser.HUNDREDS_PLACE);

        addPickerToLayout(hundredsPicker);
        addPickerToLayout(tensPicker);
        addPickerToLayout(onesPicker);
        addPickerToLayout(tenthsPicker);
        addPickerToLayout(hundredthsPicker);
    }

    private void addPickerToLayout(NumberPicker numberPicker){
        if (numberPicker.getParent() != null)
            ((ViewGroup) numberPicker.getParent()).removeView(numberPicker);
        addView(numberPicker);

        ViewGroup.LayoutParams params = numberPicker.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = 110;
        numberPicker.setLayoutParams(params);

        TextView textView = new TextView(context);
        textView.setText(" ");
        int id = numberPicker.getId();
        if(id == NumericParser.ONES_PLACE && showDecimal) {
            textView.setText(Html.fromHtml("<big>.</big>"));
            numberPicker.measure(0, 0);
            textView.setHeight(numberPicker.getMeasuredHeight());
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setTypeface(null, Typeface.BOLD);
        }
        addView(textView);
    }

    private void setPickersForNumber() {
        setPickerFor(hundredsPicker, numericParser.HundredsValue);
        setPickerFor(tensPicker, numericParser.TensValue);
        setPickerFor(onesPicker, numericParser.OnesValue);
        setPickerFor(tenthsPicker, numericParser.TenthsValue);
        setPickerFor(hundredthsPicker, numericParser.HundredthsValue);
    }

    private void setPickerFor(NumberPicker picker, int value) {
        String hundredsValue = Integer.toString(value);
        int indexOfNextValue = Arrays.asList(picker.getDisplayedValues()).indexOf(hundredsValue);
        picker.setValue(indexOfNextValue);
    }

    private NumberPicker getNumericPickerFor(int id) {
        NumberPicker picker = new NumberPicker(context);
        picker.setId(id);
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        final List<String> itemsToDisplay = new ArrayList<>();
        for (int seqValue = 0; seqValue < 10; seqValue ++) {
            itemsToDisplay.add(seqValue, Integer.toString(seqValue));
        }

        String[] values = itemsToDisplay.toArray(new String[itemsToDisplay.size()]);
        picker.setMinValue(0);
        picker.setMaxValue(values.length - 1);
        picker.setDisplayedValues(values);
        picker.setWrapSelectorWheel(false);

        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                int newValue = Integer.valueOf(itemsToDisplay.get(newVal));
                numericParser.setValue(picker.getId(), newValue);
                if (listener != null) {
                    listener.onValueChange();
                }
            }
        });

        return picker;
    }

    private OnValueChangeListener listener;
    public void setOnValueChangedListener(OnValueChangeListener listener) {
        this.listener = listener;
    }

    public interface OnValueChangeListener {
        void onValueChange();
    }
}
