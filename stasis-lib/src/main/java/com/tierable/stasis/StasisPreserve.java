package com.tierable.stasis;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.SOURCE;


/**
 * Indicates that the annotated field should be preserved using a
 * {@link StasisPreservationStrategy}.
 * <p>
 * If the {@link #value()}} is specified, it will take precedence over the
 * {@link StasisPreservationMapping}
 *
 * @author Aniruddh Fichadia
 * @date 2017-07-27
 */
@Target(FIELD)
@Retention(SOURCE)
public @interface StasisPreserve {
    Class<? extends StasisPreservationStrategy> value() default StasisPreservationStrategyAutoResolve.class;

    boolean enabled() default true;
}