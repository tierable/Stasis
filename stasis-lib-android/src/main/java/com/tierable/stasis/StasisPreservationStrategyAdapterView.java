package com.tierable.stasis;


import android.widget.Adapter;
import android.widget.AdapterView;


/**
 * @author Aniruddh Fichadia
 * @date 2017-07-27
 */
public class StasisPreservationStrategyAdapterView
        extends StasisPreservationStrategyView<AdapterView> {
    // TODO: scroll position
    private Adapter adapter;


    @Override
    public void freeze(AdapterView preserved) {
        super.freeze(preserved);

        adapter = preserved.getAdapter();
    }

    @Override
    public void unFreeze(AdapterView preserved) {
        super.unFreeze(preserved);

        preserved.setAdapter(adapter);
    }


    @Override
    public String toString() {
        return "StasisPreservationStrategyAdapterView{" +
                "adapter=" + adapter +
                "} " + super.toString();
    }
}
