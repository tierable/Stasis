package com.tierable.stasis;


/**
 * Don't preserve or restore any state
 *
 * @author Aniruddh Fichadia
 * @date 2017-07-27
 */
public final class PreservationStrategyDoNotPreserve
        implements PreservationStrategy {
    @Override
    public final void freeze(Object preserved) {
    }

    @Override
    public final void unFreeze(Object preserved) {
    }


    @Override
    public String toString() {
        return "PreservationStrategyDoNotPreserve{}";
    }
}
