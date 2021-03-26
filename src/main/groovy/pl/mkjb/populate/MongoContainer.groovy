package pl.mkjb.populate

import org.spockframework.runtime.extension.ExtensionAnnotation

import java.lang.annotation.Retention
import java.lang.annotation.Target

import static java.lang.annotation.ElementType.TYPE
import static java.lang.annotation.RetentionPolicy.RUNTIME

@Target([TYPE])
@Retention(RUNTIME)
@ExtensionAnnotation(MongoContainerExtension)
@interface MongoContainer {
    String testContainers() default "true"
}