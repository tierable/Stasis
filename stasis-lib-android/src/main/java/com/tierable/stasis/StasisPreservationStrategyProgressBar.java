package com.tierable.stasis;


import android.widget.ProgressBar;


/**
 * Preserves and restores the
 * <ul>
 * <li>Max
 * <li>Progress
 * <li>State saved by {@link StasisPreservationStrategyView}
 * </ul>
 *
 * @author Aniruddh Fichadia
 * @date 2017-07-27
 */
public class StasisPreservationStrategyProgressBar
        extends StasisPreservationStrategyView<ProgressBar> {
    private int max;
    private int progress;


    @Override
    public void freeze(ProgressBar preserved) {
        super.freeze(preserved);

        max = preserved.getMax();
        progress = preserved.getProgress();
    }

    @Override
    public void unFreeze(ProgressBar preserved) {
        super.unFreeze(preserved);

        preserved.setMax(max);
        preserved.setProgress(progress);
    }


    @Override
    public String toString() {
        return "StasisPreservationStrategyProgressBar{" +
                "max=" + max +
                ", progress=" + progress +
                "} " + super.toString();
    }
}
