package com.tierable.stasis;


/**
 * Marker class indicating that the StasisPreservationStrategy is automatically resolved using
 * the configured defaults
 *
 * @author Aniruddh Fichadia
 * @date 2017-07-27
 */
public final class StasisPreservationStrategyAutoResolve
        implements StasisPreservationStrategy {
    @Override
    public final void freeze(Object preserved) {
    }

    @Override
    public final void unFreeze(Object preserved) {
    }


    @Override
    public String toString() {
        return "StasisPreservationStrategyAutoResolve{}";
    }
}
