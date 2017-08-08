package com.tierable.stasis;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;


/**
 * Marks that the class provides the mapping of {@link StasisPreservationStrategy} to types.
 * <p>
 * This annotation is only valid for interfaces.
 *
 * @author Aniruddh Fichadia
 * @date 2017-07-27
 */
@Target(TYPE)
@Retention(CLASS)
public @interface StasisPreservationMapping {
    Class<? extends StasisPreservationStrategy> value() default StasisPreservationStrategyDoNotPreserve.class;
}