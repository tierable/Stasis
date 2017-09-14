package com.tierable.stasis.demo;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.tierable.stasis.Preserve;


public class MainActivity
        extends AppCompatActivity {
    private static final String KEY_PRESERVATION_STRATEGY = "key_preservation_strategy";


    @Preserve
    TextView     textView;
    @Preserve
    @Nullable
    TextView     nullableField;
    @Preserve(ButtonCustomPreservationStrategy.class)
    Button       buttonWithCustomPreservationStrategy;
    @Preserve
    CheckBox     checkBox;
    @Preserve
    Button       button;
    @Preserve
    RecyclerView recyclerView;
    @Preserve
    ListView     listView;


    @Nullable
    private PreservationStrategyMainActivity preservationStrategy;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null &&
                savedInstanceState.containsKey(KEY_PRESERVATION_STRATEGY)) {
            // TODO: Retrieve the PreservationStrategy from somewhere ... Ideally you
            // won't save it using the savedInstanceState directly
            preservationStrategy = (PreservationStrategyMainActivity) savedInstanceState.getSerializable(
                    KEY_PRESERVATION_STRATEGY);
        }

        if (preservationStrategy == null) {
            // TODO: Initialise the PreservationStrategy
            preservationStrategy = new PreservationStrategyMainActivity();
        } else {
            // TODO: UnFreeze/Restore the UI state
            preservationStrategy.unFreeze(this);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (preservationStrategy != null) {
            // TODO: Freeze/Save the UI state
            preservationStrategy.freeze(this);
            // TODO: Save the state somewhere ... again ... don't do thisx
            outState.putSerializable(KEY_PRESERVATION_STRATEGY, preservationStrategy);
        }
    }
}
