package com.tierable.stasis;


import android.widget.CompoundButton;


/**
 * Preserves and restores the
 * <ul>
 * <li>Checked state
 * <li>State saved by {@link StasisPreservationStrategyView}
 * </ul>
 *
 * @author Aniruddh Fichadia
 * @date 2017-07-27
 */
public class StasisPreservationStrategyCompoundButton
        extends StasisPreservationStrategyView<CompoundButton> {
    private boolean checked;


    @Override
    public void freeze(CompoundButton preserved) {
        super.freeze(preserved);

        checked = preserved.isChecked();
    }

    @Override
    public void unFreeze(CompoundButton preserved) {
        super.unFreeze(preserved);

        preserved.setChecked(checked);
    }


    @Override
    public String toString() {
        return "StasisPreservationStrategyCompoundButton{" +
                "checked=" + checked +
                "} " + super.toString();
    }
}
