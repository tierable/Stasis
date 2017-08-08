package com.tierable.stasis;


import android.os.Parcelable;
import android.widget.TextView;


/**
 * Preserves and restores the
 * <ul>
 * <li>Text
 * <li>Selection info
 * <li>State saved by {@link StasisPreservationStrategyView}
 * </ul>
 *
 * @author Aniruddh Fichadia
 * @date 2017-07-27
 */
public class StasisPreservationStrategyTextView
        extends StasisPreservationStrategyView<TextView> {
    private Parcelable frozenProperties;


    @Override
    public void freeze(TextView preserved) {
        super.freeze(preserved);

        boolean oldFreezeFlag = preserved.getFreezesText();
        preserved.setFreezesText(true);
        frozenProperties = preserved.onSaveInstanceState();
        preserved.setFreezesText(oldFreezeFlag);
    }

    @Override
    public void unFreeze(TextView preserved) {
        super.unFreeze(preserved);

        boolean oldFreezeFlag = preserved.getFreezesText();
        preserved.setFreezesText(true);
        preserved.onRestoreInstanceState(frozenProperties);
        preserved.setFreezesText(oldFreezeFlag);
    }


    @Override
    public String toString() {
        return "StasisPreservationStrategyTextView{" +
                "frozenProperties=" + frozenProperties +
                "} " + super.toString();
    }
}
