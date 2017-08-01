package com.tierable.stasis.demo;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.tierable.stasis.StasisPreserve;


public class MainActivity
        extends AppCompatActivity {
    private static final String KEY_PRESERVATION_STRATEGY = "key_preservation_strategy";


    @StasisPreserve
    TextView textView;
    @StasisPreserve
    CheckBox checkBox;
    @StasisPreserve
    Button   button;

    @StasisPreserve(ButtonCustomStasisPreservationStrategy.class)
    Button       button2;
    @StasisPreserve
    RecyclerView recyclerView;

    @Nullable
    private StasisPreservationStrategyMainActivity preservationStrategy;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null &&
                savedInstanceState.containsKey(KEY_PRESERVATION_STRATEGY)) {
            // TODO: Retrieve the StasisPreservationStrategy from somewhere ... Ideally you
            // won't save it using the savedInstanceState directly
            preservationStrategy = (StasisPreservationStrategyMainActivity) savedInstanceState.getSerializable(
                    KEY_PRESERVATION_STRATEGY);
        }

        if (preservationStrategy == null) {
            // TODO: Initialise the StasisPreservationStrategy
            preservationStrategy = new StasisPreservationStrategyMainActivity();
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
