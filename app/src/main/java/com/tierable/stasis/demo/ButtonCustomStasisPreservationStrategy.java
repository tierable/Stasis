package com.tierable.stasis.demo;


import android.widget.Button;

import com.tierable.stasis.StasisPreservationStrategy;


/**
 * @author Aniruddh Fichadia
 * @date 2017-07-28
 */
public class ButtonCustomStasisPreservationStrategy
        implements StasisPreservationStrategy<Button> {
    @Override
    public void freeze(Button preserved) {
        // Do something
    }

    @Override
    public void unFreeze(Button preserved) {
        // Do something
    }
}
