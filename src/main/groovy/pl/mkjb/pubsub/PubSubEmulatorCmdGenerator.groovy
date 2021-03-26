package pl.mkjb.pubsub

import java.util.stream.Collectors

class PubSubEmulatorCmdGenerator {
    def private static final CURL_COMMAND = """curl -s -X PUT -H "Content-type: application/json" http://localhost:8085/v1/"""
    def private static final TOPIC_RESOURCE = "projects/test-local/topics/%s"
    def private static final SUBSCRIPTION_RESOURCE = "projects/test-local/subscriptions/%s"
    def private static final TOPIC_RESOURCE_PAYLOAD = """ --data '\\''{"topic":"%s"}'\\''"""
    def private static final SHELL_CMD = '/bin/sh'
    def private static final INLINE_SCRIPT_FLAG = '-c'
    def private static final POPULATE_CMD =
                    """
                        /bin/sh -c 'sleep 2
                         while :
                          do
                            %s
                            %s
                          done &' ;
                          gcloud beta emulators pubsub start --host-port 0.0.0.0:8085
                   """

    def private static createTopicCmd(String topicName) {
        CURL_COMMAND + String.format(TOPIC_RESOURCE, topicName)
    }

    def private static createSubscriptionCmd(String subscriptionName, String topicName) {
        def topicResource = String.format(TOPIC_RESOURCE, topicName)
        CURL_COMMAND + String.format(SUBSCRIPTION_RESOURCE, subscriptionName) + String.format(TOPIC_RESOURCE_PAYLOAD, topicResource)
    }

    static String[] containerStartupCommand(Set<String> topics, Set<Tuple2<String, String>> subscriptions) {
        def topicCmds = toTopicCommands(topics)
        def subscriptionCmds = toSubscriptionCommands(subscriptions)
        def populateCmd = String.format(POPULATE_CMD, topicCmds, subscriptionCmds)

        [SHELL_CMD, INLINE_SCRIPT_FLAG, populateCmd]
    }

    def static toTopicCommands(Set<String> topics) {
        topics
                .stream()
                .map(topic -> createTopicCmd(topic))
                .collect(Collectors.joining("\n"))
    }

    def static toSubscriptionCommands(Set<Tuple2<String, String>> subscriptions) {
        subscriptions
                .stream()
                .map(subscription -> createSubscriptionCmd(subscription.getV1(), subscription.getV2()))
                .collect(Collectors.joining("\n"))
    }
}
