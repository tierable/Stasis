package com.tierable.stasis;


import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;


/**
 * Sensible defaults for Android. Extend this interface in your {@link PreservationMapping}
 * if required.
 *
 * @author Aniruddh Fichadia
 * @date 2017-07-27
 */
public interface AndroidPreservationMapping {
    PreservationStrategyTextView getStrategy(
            TextView textView, AppCompatTextView appCompatTextView, EditText editText,
            AppCompatEditText appCompatEditText, Button button, AppCompatButton appCompatButton
    );

    PreservationStrategyCompoundButton getStrategy(
            CompoundButton compoundButton, CheckBox checkBox, AppCompatCheckBox appCompatCheckBox,
            ToggleButton toggleButton, RadioButton radioButton, AppCompatRadioButton appCompatRadioButton
    );

    PreservationStrategyTextInputLayout getStrategy(
            TextInputLayout textInputLayout
    );


    PreservationStrategyAdapterView getStrategy(
            AdapterView adapterView, ListView listView, ExpandableListView expandableListView, Spinner spinner,
            AppCompatSpinner appCompatSpinner
    );

    PreservationStrategyRecyclerView getStrategy(
            RecyclerView recyclerView
    );


    PreservationStrategyProgressBar getStrategy(
            ProgressBar ProgressBar
    );
}