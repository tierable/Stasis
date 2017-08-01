package com.tierable.stasis;


import android.support.v7.widget.RecyclerView;


/**
 * @author Aniruddh Fichadia
 * @date 2017-07-27
 */
public class StasisPreservationStrategyRecyclerView
        extends StasisPreservationStrategyView<RecyclerView> {
    private RecyclerView.Adapter adapter;
    // TODO: layout manager?


    @Override
    public void freeze(RecyclerView preserved) {
        super.freeze(preserved);

        adapter = preserved.getAdapter();
    }

    @Override
    public void unFreeze(RecyclerView preserved) {
        super.unFreeze(preserved);

        preserved.setAdapter(adapter);
    }


    @Override
    public String toString() {
        return "StasisPreservationStrategyRecyclerView{" +
                "adapter=" + adapter +
                "} " + super.toString();
    }
}
