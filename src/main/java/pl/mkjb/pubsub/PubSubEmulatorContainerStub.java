package pl.mkjb.pubsub;


import groovy.lang.Tuple2;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.exceptions.HttpClientException;
import io.micronaut.http.client.netty.DefaultHttpClient;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.groovy.util.Maps;

import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static io.micronaut.http.HttpMethod.GET;
import static io.micronaut.http.HttpMethod.PUT;
import static io.micronaut.http.HttpStatus.OK;

@Slf4j
@EqualsAndHashCode(callSuper = true)
public class PubSubEmulatorContainerStub extends PubSubEmulatorContainer {
    private static final String PUB_SUB_PROTOCOL = "http://";
    private static final String PUB_SUB_HOST = "localhost:8085";
    private static final String GCP_PROJECT_ID = "test-local";
    private static final String TOPIC_RESOURCE = "projects/" + GCP_PROJECT_ID + "/topics/%s";
    private static final String TOPICS_RESOURCE = "projects/" + GCP_PROJECT_ID + "/topics";
    private static final String SUBSCRIPTION_RESOURCE = "projects/" + GCP_PROJECT_ID + "/subscriptions/%s";

    private final RxHttpClient httpClient;
    private final Set<String> topics = new HashSet<>();
    private final Set<Tuple2<String, String>> subscriptions = new HashSet<>();

    @SneakyThrows
    public PubSubEmulatorContainerStub() {
        this.httpClient = new DefaultHttpClient(new URL(PUB_SUB_PROTOCOL + PUB_SUB_HOST + "/v1/"));
    }

    @Override
    public PubSubEmulatorContainerStub withTopic(Topic topic) {
        this.topics.add(topic.value());
        return this;
    }

    @Override
    public PubSubEmulatorContainerStub withSubscription(Subscription subscription) {
        this.subscriptions.add(new Tuple2<>(subscription.subscription(), subscription.topic()));
        return this;
    }

    @Override
    public boolean isCreated() {
        return true;
    }

    @SneakyThrows
    @Override
    public boolean isRunning() {
        if (!isLocalInstanceRunning()) {
            throw new IllegalStateException("Local instance of Google Pub Sub Emulator is not running on localhost:8085");
        }

        topics.forEach(topic -> {
            if (shouldBeCreated(TOPIC_RESOURCE, topic)) {
                MutableHttpRequest<Object> request = HttpRequest.create(PUT, String.format(TOPIC_RESOURCE, topic));
                httpClient.retrieve(request).blockingLast();
            }
        });

        subscriptions.forEach(subscription -> {
            if (shouldBeCreated(SUBSCRIPTION_RESOURCE, subscription.getV1())) {
                Map<String, String> payload = Maps.of("topic", String.format(TOPIC_RESOURCE, subscription.getV2()));
                MutableHttpRequest<Map<String, String>> request = HttpRequest.PUT(String.format(SUBSCRIPTION_RESOURCE, subscription.getV1()), payload);
                httpClient.retrieve(request).blockingLast();
            }
        });

        return true;
    }

    private boolean shouldBeCreated(String resourceType, String resourceName) {
        try {
            return !httpClient.exchange(HttpRequest.GET(String.format(resourceType, resourceName))).blockingLast().status().equals(OK);
        } catch (HttpClientException ignored) {
            return true;
        }
    }

    public boolean isLocalInstanceRunning() {
        try {
            MutableHttpRequest<Object> request = HttpRequest.create(GET, TOPICS_RESOURCE);
            return httpClient.exchange(request).blockingLast().status().equals(OK);
        } catch (HttpClientException ignored) {
            return false;
        }
    }

    @Override
    public String getEmulatorEndpoint() {
        return PUB_SUB_HOST;
    }

    @Override
    public String getGcpProjectId() {
        return GCP_PROJECT_ID;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void close() {
    }
}
