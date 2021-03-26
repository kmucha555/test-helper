package pl.mkjb.pubsub


import java.lang.annotation.Retention
import java.lang.annotation.Target

import static java.lang.annotation.ElementType.TYPE
import static java.lang.annotation.RetentionPolicy.RUNTIME

@Target([TYPE])
@Retention(RUNTIME)
@interface Topics {
    Topic[] value() default [];
}