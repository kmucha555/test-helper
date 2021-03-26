package pl.mkjb.pubsub

import org.spockframework.runtime.extension.ExtensionAnnotation

import java.lang.annotation.Retention
import java.lang.annotation.Target

import static java.lang.annotation.ElementType.TYPE
import static java.lang.annotation.RetentionPolicy.RUNTIME

@Target([TYPE])
@Retention(RUNTIME)
@ExtensionAnnotation(PubSubExtension.class)
@interface PubSub {
    String testContainers() default "true"

    Topics topics();

    Subscriptions subscriptions();
}