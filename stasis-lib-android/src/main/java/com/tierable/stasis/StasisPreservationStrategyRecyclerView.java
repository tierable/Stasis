package com.tierable.stasis;


import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;


/**
 * @author Aniruddh Fichadia
 * @date 2017-07-27
 */
public class StasisPreservationStrategyRecyclerView
        extends StasisPreservationStrategyView<RecyclerView> {
    private RecyclerView.Adapter adapter;
    private int                  firstVisibleItemPosition;


    @Override
    public void freeze(RecyclerView preserved) {
        super.freeze(preserved);

        LayoutManager layoutManager = preserved.getLayoutManager();
        adapter = preserved.getAdapter();
        if (layoutManager instanceof GridLayoutManager) {
            firstVisibleItemPosition = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
        } else if (layoutManager instanceof LinearLayoutManager) {
            firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        }
    }

    @Override
    public void unFreeze(RecyclerView preserved) {
        super.unFreeze(preserved);

        preserved.setAdapter(adapter);
        LayoutManager layoutManager = preserved.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            preserved.scrollToPosition(firstVisibleItemPosition);
        } else if (layoutManager instanceof LinearLayoutManager) {
            preserved.scrollToPosition(firstVisibleItemPosition);
        }
    }


    @Override
    public String toString() {
        return "StasisPreservationStrategyRecyclerView{" +
                "adapter=" + adapter +
                ", firstVisibleItemPosition=" + firstVisibleItemPosition +
                "} " + super.toString();
    }
}
