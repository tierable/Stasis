package com.tierable.stasis;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;


/**
 * @author Aniruddh Fichadia
 * @date 2017-07-27
 */
@Target(TYPE)
@Retention(CLASS)
public @interface StasisPreservationMapping {
    Class<? extends StasisPreservationStrategy> value() default StasisPreservationStrategyDoNotPreserve.class;
}