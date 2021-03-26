package pl.mkjb.populate


import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.spockframework.runtime.extension.AbstractMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation
import org.spockframework.runtime.model.FieldInfo
import org.spockframework.runtime.model.SpecInfo
import org.testcontainers.containers.MongoDBContainer

class MongoContainerInterceptor extends AbstractMethodInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(MongoContainerInterceptor)
    private final String MONGO_VERSION = "mongo:4.2.10";
    private final SpecInfo spec
    private final String testContainer

    MongoContainerInterceptor(SpecInfo spec, String testContainer) {
        this.spec = spec
        this.testContainer = testContainer
    }

    @Override
    void interceptSharedInitializerMethod(IMethodInvocation invocation) throws Throwable {
        def mongoDbContainerField = findMongoContainer(true)
        mongoDbContainerField.writeValue(invocation.instance, getContainer())

        invocation.proceed()
    }

    def private getContainer() {
        if (testContainer == 'true') {
            LOG.info("Using MongoDB instance from Testcontainers")
            return new MongoDBContainer(MONGO_VERSION)
        }

        LOG.info("Using MongoDB instance from localhost")
        return new MongoDBContainerStub()
    }

    private FieldInfo findMongoContainer(boolean shared) {
        spec.allFields.find { FieldInfo f ->
            MongoDBContainer.isAssignableFrom(f.type) && f.shared == shared
        }
    }
}
