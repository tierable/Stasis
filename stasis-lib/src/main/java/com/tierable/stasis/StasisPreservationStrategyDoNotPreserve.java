package com.tierable.stasis;


/**
 * Don't save any state
 *
 * @author Aniruddh Fichadia
 * @date 2017-07-27
 */
public final class StasisPreservationStrategyDoNotPreserve
        implements StasisPreservationStrategy {
    @Override
    public final void freeze(Object preserved) {
    }

    @Override
    public final void unFreeze(Object preserved) {
    }


    @Override
    public String toString() {
        return "StasisPreservationStrategyDoNotPreserve{}";
    }
}
