package com.tierable.stasis;


import android.view.View;


/**
 * Preserves and restores the
 * <ul>
 * <li>visibility
 * <li>enabled
 * <li>selected
 * <li>contentDescription
 * </ul>
 *
 * @author Aniruddh Fichadia
 * @date 2017-07-27
 */
public class StasisPreservationStrategyView<ViewT extends View>
        implements StasisPreservationStrategy<ViewT> {
    private int          visibility;
    private boolean      enabled;
    private boolean      selected;
    private CharSequence contentDescription;


    @Override
    public void freeze(ViewT preserved) {
        visibility = preserved.getVisibility();
        enabled = preserved.isEnabled();
        selected = preserved.isSelected();
        contentDescription = preserved.getContentDescription();
    }

    @Override
    public void unFreeze(ViewT preserved) {
        preserved.setVisibility(visibility);
        preserved.setEnabled(enabled);
        preserved.setSelected(selected);
        preserved.setContentDescription(contentDescription);
    }


    @Override
    public String toString() {
        return "StasisPreservationStrategyView{" +
                "visibility=" + visibility +
                ", enabled=" + enabled +
                ", selected=" + selected +
                ", contentDescription=" + contentDescription +
                '}';
    }
}
