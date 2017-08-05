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
    private LayoutManager        layoutManager;
    private RecyclerView.Adapter adapter;
    private int                  firstVisibleItemPosition;


    @Override
    public void freeze(RecyclerView preserved) {
        super.freeze(preserved);

        layoutManager = preserved.getLayoutManager();
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

        preserved.setLayoutManager(layoutManager);
        preserved.setAdapter(adapter);
        if (layoutManager instanceof GridLayoutManager) {
            preserved.scrollToPosition(firstVisibleItemPosition);
        } else if (layoutManager instanceof LinearLayoutManager) {
            preserved.scrollToPosition(firstVisibleItemPosition);
        }
    }


    @Override
    public String toString() {
        return "StasisPreservationStrategyRecyclerView{" +
                "layoutManager=" + layoutManager +
                ", adapter=" + adapter +
                ", firstVisibleItemPosition=" + firstVisibleItemPosition +
                "} " + super.toString();
    }
}
