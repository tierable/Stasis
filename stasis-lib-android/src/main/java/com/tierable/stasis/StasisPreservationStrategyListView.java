package com.tierable.stasis;


import android.widget.ListAdapter;
import android.widget.ListView;


/**
 * @author Aniruddh Fichadia
 * @date 2017-07-27
 */
public class StasisPreservationStrategyListView
        extends StasisPreservationStrategyView<ListView> {
    private ListAdapter adapter;


    @Override
    public void freeze(ListView preserved) {
        super.freeze(preserved);

        adapter = preserved.getAdapter();
    }

    @Override
    public void unFreeze(ListView preserved) {
        super.unFreeze(preserved);

        preserved.setAdapter(adapter);
    }


    @Override
    public String toString() {
        return "StasisPreservationStrategyListView{" +
                "adapter=" + adapter +
                "} " + super.toString();
    }
}
