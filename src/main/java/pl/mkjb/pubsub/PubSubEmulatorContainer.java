package pl.mkjb.pubsub;

import groovy.lang.Tuple2;
import lombok.EqualsAndHashCode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
public class PubSubEmulatorContainer extends GenericContainer<PubSubEmulatorContainer> {
    private static final DockerImageName PUB_SUB_VERSION = DockerImageName.parse("gcr.io/google.com/cloudsdktool/cloud-sdk:317.0.0-emulators");
    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse("gcr.io/google.com/cloudsdktool/cloud-sdk");
    private static final int PORT = 8085;
    private static final int LOG_DUPLICATED_TIMES = 4;
    private static final String GCP_PROJECT_ID = "test-local";
    private final Set<String> topics = new HashSet<>();
    private final Set<Tuple2<String, String>> subscriptions = new HashSet<>();

    public PubSubEmulatorContainer() {
        super(PUB_SUB_VERSION);
        PUB_SUB_VERSION.assertCompatibleWith(DEFAULT_IMAGE_NAME);
        this.withExposedPorts(PORT);
        this.setWaitStrategy(Wait.forLogMessage("(?s).*already exists.*$", LOG_DUPLICATED_TIMES));
    }

    public PubSubEmulatorContainer withTopic(Topic topic) {
        this.topics.add(topic.value());
        return this;
    }

    public PubSubEmulatorContainer withSubscription(Subscription subscription) {
        this.subscriptions.add(new Tuple2<>(subscription.subscription(), subscription.topic()));
        return this;
    }

    @Override
    public void start() {
        this.setCommand(PubSubEmulatorCmdGenerator.containerStartupCommand(topics, subscriptions));
        super.start();
    }

    public String getEmulatorEndpoint() {
        return this.getContainerIpAddress() + ":" + this.getMappedPort(PORT);
    }

    public String getGcpProjectId() {
        return GCP_PROJECT_ID;
    }
}
