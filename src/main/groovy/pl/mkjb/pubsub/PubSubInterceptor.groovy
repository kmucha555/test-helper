package pl.mkjb.pubsub


import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.spockframework.runtime.extension.AbstractMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation
import org.spockframework.runtime.model.FieldInfo

class PubSubInterceptor extends AbstractMethodInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(PubSubInterceptor.class);
    private final PubSubExtension.TestProperty testProperty
    private final PubSubExtension.ErrorListener errorListener

    PubSubInterceptor(PubSubExtension.TestProperty testProperty, PubSubExtension.ErrorListener errorListener) {
        this.testProperty = testProperty
        this.errorListener = errorListener
    }

    @Override
    void interceptSharedInitializerMethod(IMethodInvocation invocation) throws Throwable {
        def pubSubContainerField = findPubSubContainerField(invocation, true)
        def pubSubEmulatorContainer = getContainer()

        populateWithTopics(pubSubEmulatorContainer, testProperty.topics)
        populateWithSubscription(pubSubEmulatorContainer, testProperty.subscription)

        pubSubContainerField.writeValue(invocation.sharedInstance, pubSubEmulatorContainer)
        invocation.proceed()
    }

    def private getContainer() {
        if (testProperty.testContainers == 'true') {
            LOG.info("Using PubSub Emulator instance from Testcontainers")
            return new PubSubEmulatorContainer()
        }

        LOG.info("Using PubSub Emulator instance from localhost")
        return new PubSubEmulatorContainerStub()
    }

    private static def populateWithTopics(PubSubEmulatorContainer container, List<Topics> topics) {
        topics.each { topic ->
            {
                LOG.info("Topic '{}' added to PubSub Emulator configuration", topic)
                container.withTopic(topic)
            }
        }
    }

    private static def populateWithSubscription(PubSubEmulatorContainer container, List<Subscription> subscriptions) {
        subscriptions.each { subscription ->
            {
                LOG.info("Subscription '{}' to topic '{}' added to PubSub Emulator configuration", subscription.subscription(), subscription.topic())
                container.withSubscription(subscription)
            }
        }
        Tuple2<String, String>
    }

    private static FieldInfo findPubSubContainerField(IMethodInvocation invocation, boolean shared) {
        invocation.getSpec().allFields.find { FieldInfo f ->
            PubSubEmulatorContainer.isAssignableFrom(f.type) && f.shared == shared
        }
    }
}
