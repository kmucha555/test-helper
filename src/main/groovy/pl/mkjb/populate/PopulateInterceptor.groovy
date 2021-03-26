package pl.mkjb.populate

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.spockframework.runtime.extension.AbstractMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation
import org.spockframework.runtime.model.FieldInfo
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.utility.MountableFile

import java.util.stream.IntStream

class PopulateInterceptor extends AbstractMethodInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(PopulateInterceptor)
    private final PopulateExtension.TestProperty testProperty

    PopulateInterceptor(PopulateExtension.TestProperty testProperty) {
        this.testProperty = testProperty
    }

    @Override
    void interceptSetupSpecMethod(final IMethodInvocation invocation) throws Throwable {
        populate(invocation)
        invocation.proceed()
    }

    @Override
    void interceptFeatureExecution(final IMethodInvocation invocation) throws Throwable {
        populate(invocation)
        invocation.proceed()
    }

    private void populate(IMethodInvocation invocation) {
        def mongoDbContainerField = findMongoContainer(invocation, true)
        def mongoDbContainer = readContainerFromField(mongoDbContainerField, invocation)

        if (mongoDbContainer.isRunning()) {
            IntStream.range(0, testProperty.populateFrom.size())
                    .forEach(idx -> populateFromJson(mongoDbContainer, testProperty.populateFrom[idx], testProperty.dbColl[idx]))
        }
    }

    private void populateFromJson(MongoDBContainer container, String sourceFile, String collectionName) {
        def mountableFile = MountableFile.forClasspathResource(sourceFile)
        LOG.info('Populating MongoDB. DB: {}, collection: {}, input file: {}', testProperty.dbName, collectionName, mountableFile.getFilesystemPath())

        def containerPath = "/docker-entrypoint-initdb.d/$collectionName/$sourceFile"
        container.copyFileToContainer(mountableFile, containerPath)
        container.execInContainer(importCommand(collectionName, containerPath))
    }

    private String[] importCommand(String collectionName, String containerPath) {
        new String[]
                {
                        "mongoimport",
                        "--db=$testProperty.dbName",
                        "--collection=$collectionName",
                        "--jsonArray",
                        "--drop",
                        "--file=/$containerPath"
                }
    }

    private static FieldInfo findMongoContainer(IMethodInvocation invocation, boolean shared) {
        invocation.getSpec().allFields.find { FieldInfo f ->
            MongoDBContainer.isAssignableFrom(f.type) && f.shared == shared
        }
    }

    private static MongoDBContainer readContainerFromField(FieldInfo f, IMethodInvocation invocation) {
        f.readValue(invocation.sharedInstance) as MongoDBContainer
    }
}
