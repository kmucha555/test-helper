package pl.mkjb.pubsub;

import com.google.api.gax.core.*;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.pubsub.v1.Publisher;
import io.grpc.ManagedChannelBuilder;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Requires;
import io.micronaut.gcp.Modules;
import io.micronaut.gcp.UserAgentHeaderProvider;
import io.micronaut.gcp.pubsub.configuration.PubSubConfigurationProperties;
import org.threeten.bp.Duration;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.concurrent.Executors;

@Factory
@Primary
@Requires(env = "test", classes = Publisher.class)
public class PubSubConfigurationFactory {
    private final PubSubConfigurationProperties pubSubConfigurationProperties;
    private final PubSubEmulatorSettings pubSubEmulatorSettings;

    public PubSubConfigurationFactory(final PubSubConfigurationProperties pubSubConfigurationProperties,
                                      final PubSubEmulatorSettings pubSubEmulatorSettings) {
        this.pubSubConfigurationProperties = pubSubConfigurationProperties;
        this.pubSubEmulatorSettings = pubSubEmulatorSettings;
    }

    @Singleton
    @Primary
    public ExecutorProvider publisherExecutorProvider() {
        //TODO needs to provide better scheduled executor
        return FixedExecutorProvider.create(Executors.newScheduledThreadPool(1));
    }

    @Singleton
    @Primary
    @Named(Modules.PUBSUB)
    @Requires(missingProperty = PubSubEmulatorSettings.EMULATOR + ".host")
    public TransportChannelProvider transportChannelProvider() {
        return InstantiatingGrpcChannelProvider.newBuilder()
                .setHeaderProvider(new UserAgentHeaderProvider(Modules.PUBSUB))
                .setKeepAliveTime(Duration.ofMinutes(this.pubSubConfigurationProperties.getKeepAliveIntervalMinutes()))
                .build();
    }

    @Singleton
    @Primary
    @Named(Modules.PUBSUB)
    @Requires(property = PubSubEmulatorSettings.EMULATOR + ".host")
    public TransportChannelProvider localChannelProvider() {
        final String pubSubUrl = pubSubEmulatorSettings.getUrl();
        return FixedTransportChannelProvider.create(GrpcTransportChannel.create(ManagedChannelBuilder.forTarget(pubSubUrl).usePlaintext().build()));
    }

    @Singleton
    @Primary
    @Named(Modules.PUBSUB)
    @Requires(missingProperty = PubSubEmulatorSettings.EMULATOR + ".host")
    public CredentialsProvider credentialsProvider(GoogleCredentials credentials) {
        return FixedCredentialsProvider.create(credentials);
    }

    @Singleton
    @Primary
    @Named(Modules.PUBSUB)
    @Requires(property = PubSubEmulatorSettings.EMULATOR + ".host")
    public CredentialsProvider noCredentialsProvider() {
        return NoCredentialsProvider.create();
    }
}
