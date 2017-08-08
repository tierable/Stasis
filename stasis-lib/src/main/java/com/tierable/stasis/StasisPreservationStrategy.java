package com.tierable.stasis;


import java.io.Serializable;


/**
 * Saves, retains, and restores the state of something
 *
 * @author Aniruddh Fichadia
 * @date 2017-07-27
 */
public interface StasisPreservationStrategy<PreservedT>
        extends Serializable {
    /** Freeze/Save the state of the intended target */
    void freeze(PreservedT preserved);

    /** UnFreeze/restore the state of the intended target */
    void unFreeze(PreservedT preserved);
}