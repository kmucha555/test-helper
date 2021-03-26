package pl.mkjb.pubsub

import java.lang.annotation.Repeatable
import java.lang.annotation.Retention

import static java.lang.annotation.RetentionPolicy.RUNTIME

@Retention(RUNTIME)
@Repeatable(value = Subscriptions.class)
@interface Subscription {
    String subscription();
    String topic();
}