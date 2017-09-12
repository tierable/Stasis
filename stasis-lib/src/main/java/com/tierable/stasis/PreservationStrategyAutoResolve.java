package com.tierable.stasis;


/**
 * Indicates that the {@link PreservationStrategy} should be automatically resolved using the
 * {@link PreservationMapping}
 *
 * @author Aniruddh Fichadia
 * @date 2017-07-27
 */
public final class PreservationStrategyAutoResolve
        implements PreservationStrategy {
    @Override
    public final void freeze(Object preserved) {
    }

    @Override
    public final void unFreeze(Object preserved) {
    }


    @Override
    public String toString() {
        return "PreservationStrategyAutoResolve{}";
    }
}
