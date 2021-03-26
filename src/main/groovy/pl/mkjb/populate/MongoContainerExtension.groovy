package pl.mkjb.populate

import org.spockframework.runtime.AbstractRunListener
import org.spockframework.runtime.extension.IAnnotationDrivenExtension
import org.spockframework.runtime.model.ErrorInfo
import org.spockframework.runtime.model.SpecInfo

class MongoContainerExtension implements IAnnotationDrivenExtension<MongoContainer> {

    @Override
    void visitSpecAnnotation(MongoContainer annotation, SpecInfo spec) {
        def listener = new ErrorListener()
        def testContainer = annotation.testContainers()
        def interceptor = new MongoContainerInterceptor(spec, testContainer)

        spec.addSharedInitializerInterceptor(interceptor)
        spec.addListener(listener)
    }

    def class ErrorListener extends AbstractRunListener {
        List<ErrorInfo> errors = []

        @Override
        void error(ErrorInfo error) {
            errors.add(error)
        }
    }
}
