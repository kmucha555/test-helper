package pl.mkjb.pubsub

import java.lang.annotation.Repeatable
import java.lang.annotation.Retention

import static java.lang.annotation.RetentionPolicy.RUNTIME

@Retention(RUNTIME)
@Repeatable(value = Topics.class)
@interface Topic {
    String value();
}