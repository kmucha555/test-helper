package pl.mkjb.pubsub


import org.spockframework.runtime.AbstractRunListener
import org.spockframework.runtime.extension.IAnnotationDrivenExtension
import org.spockframework.runtime.model.ErrorInfo
import org.spockframework.runtime.model.SpecInfo

class PubSubExtension implements IAnnotationDrivenExtension<PubSub> {

    @Override
    void visitSpecAnnotation(PubSub annotation, SpecInfo spec) {
        def testProperty = new TestProperty(annotation)
        def listener = new ErrorListener()
        def interceptor = new PubSubInterceptor(testProperty, listener)

        spec.addSharedInitializerInterceptor(interceptor)
        spec.addListener(listener)
    }

    def class TestProperty {
        String testContainers
        List<Topic> topics
        List<Subscription> subscription

        TestProperty(PubSub annotation) {
            def testContainers = annotation.testContainers()
            def topics = Arrays.asList(annotation.topics().value())
            def subscription = Arrays.asList(annotation.subscriptions().value())

            this.testContainers = testContainers
            this.topics = topics
            this.subscription = subscription
        }
    }

    def class ErrorListener extends AbstractRunListener {
        List<ErrorInfo> errors = []

        @Override
        void error(ErrorInfo error) {
            errors.add(error)
        }
    }
}
