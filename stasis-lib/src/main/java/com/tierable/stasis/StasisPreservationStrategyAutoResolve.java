package com.tierable.stasis;


/**
 * Indicates that the {@link StasisPreservationStrategy} should be automatically resolved using
 * the {@link StasisPreservationMapping}
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
