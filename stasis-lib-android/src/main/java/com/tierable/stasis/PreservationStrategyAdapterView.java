package com.tierable.stasis;


import android.widget.Adapter;
import android.widget.AdapterView;


/**
 * Preserves and restores the
 * <ul>
 * <li>Adapter
 * <li>Selected item position
 * <li>State saved by {@link PreservationStrategyView}
 * </ul>
 *
 * @author Aniruddh Fichadia
 * @date 2017-07-27
 */
public class PreservationStrategyAdapterView<AdapterViewT extends AdapterView>
        extends PreservationStrategyView<AdapterViewT> {
    private Adapter adapter;
    private int     selectedItemPosition;


    @Override
    public void freeze(AdapterViewT preserved) {
        super.freeze(preserved);

        adapter = preserved.getAdapter();
        selectedItemPosition = preserved.getSelectedItemPosition();
    }

    @Override
    public void unFreeze(AdapterViewT preserved) {
        super.unFreeze(preserved);

        preserved.setAdapter(adapter);
        preserved.setSelection(selectedItemPosition);
    }


    @Override
    public String toString() {
        return "PreservationStrategyAdapterView{" +
                "adapter=" + adapter +
                ", selectedItemPosition=" + selectedItemPosition +
                "} " + super.toString();
    }
}
