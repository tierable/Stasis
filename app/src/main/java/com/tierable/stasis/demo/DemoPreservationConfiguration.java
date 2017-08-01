package com.tierable.stasis.demo;


import com.tierable.stasis.AndroidViewPreservationConfiguration;
import com.tierable.stasis.StasisPreservationMapping;
import com.tierable.stasis.StasisPreservationStrategyView;


/**
 * @author Aniruddh Fichadia
 * @date 2017-07-27
 */
@StasisPreservationMapping(StasisPreservationStrategyView.class)
public interface DemoPreservationConfiguration
        extends AndroidViewPreservationConfiguration {
}
