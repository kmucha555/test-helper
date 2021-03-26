package pl.mkjb.populate

import org.spockframework.runtime.extension.ExtensionAnnotation

import java.lang.annotation.Retention
import java.lang.annotation.Target

import static java.lang.annotation.ElementType.METHOD
import static java.lang.annotation.ElementType.TYPE
import static java.lang.annotation.RetentionPolicy.RUNTIME

@Target([TYPE, METHOD])
@Retention(RUNTIME)
@ExtensionAnnotation(PopulateExtension)
@interface Populate {
    String[] from() default ['mongo-init.json']
    String db() default 'test'
    String[] coll() default ['test-collection']
}