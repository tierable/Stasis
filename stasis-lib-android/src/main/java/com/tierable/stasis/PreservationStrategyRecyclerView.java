package com.tierable.stasis;


import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;


/**
 * Preserves and restores the
 * <ul>
 * <li>Adapter
 * <li>First visible item position
 * <li>State saved by {@link PreservationStrategyView}
 * </ul>
 * <p>
 * Note: Make sure {@link android.support.v7.widget.RecyclerView.AdapterDataObserver}'s are registered and
 * deregistered appropriately
 *
 * @author Aniruddh Fichadia
 * @date 2017-07-27
 */
public class PreservationStrategyRecyclerView
        extends PreservationStrategyView<RecyclerView> {
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
        return "PreservationStrategyRecyclerView{" +
                "adapter=" + adapter +
                ", firstVisibleItemPosition=" + firstVisibleItemPosition +
                "} " + super.toString();
    }
}
