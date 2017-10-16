package com.tierable.stasis;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;


/**
 * Marks that the class provides the mapping of {@link PreservationStrategy} to types.
 * <p>
 * This annotation is only valid for interfaces.
 *
 * @author Aniruddh Fichadia
 * @date 2017-07-27
 */
@Target(TYPE)
@Retention(SOURCE)
public @interface PreservationMapping {
    Class<? extends PreservationStrategy> value() default PreservationStrategyDoNotPreserve.class;
}