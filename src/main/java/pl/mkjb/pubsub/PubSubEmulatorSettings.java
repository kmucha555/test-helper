package pl.mkjb.pubsub;

import io.micronaut.context.annotation.*;
import io.micronaut.gcp.pubsub.configuration.PubSubConfigurationProperties;

import javax.inject.Singleton;

@Requires(env = "test")
@Singleton
public class PubSubEmulatorSettings {
    public static final String EMULATOR = PubSubConfigurationProperties.PREFIX + ".emulator";

    private static final String DEFAULT_URL = "localhost:8085";

    @Property(name = PubSubEmulatorSettings.EMULATOR + ".host")
    private String url = DEFAULT_URL;

    void setUrl(final String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
