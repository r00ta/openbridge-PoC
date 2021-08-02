package com.redhat.developer;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.developer.consumer.EventConsumer;
import com.redhat.developer.models.Subscription;
import com.redhat.developer.models.Topic;
import com.redhat.developer.producer.EventProducer;
import io.cloudevents.CloudEvent;
import io.cloudevents.jackson.JsonCloudEventData;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import org.jboss.logging.Logger;

@ApplicationScoped
public class EventServiceImpl implements EventService {
    private final Logger logger = Logger.getLogger(EventServiceImpl.class);

    @Inject
    EventProducer producer;

    @Inject
    TopicService topicService;

    @Inject
    Vertx vertx;

    @Override
    public void process(CloudEvent cloudEvent){
        logger.info(String.format("[CE id = %s] Start processing", cloudEvent.getId()));
        String topicName = cloudEvent.getExtension("topic").toString();
        Topic topic = topicService.getTopic(topicName);
        logger.info(String.format("[CE id = %s] There are %d available subscriptions", cloudEvent.getId(), topic.getSubscriptions().size()));
        for (Subscription subscription : topic.getSubscriptions()){
            logger.info(String.format("[CE id = %s] Processing subscription %s with endpoint %s", cloudEvent.getId(), subscription.getName(), subscription.getEndpoint()));
            WebClient client = getClient(subscription.getEndpoint());
            JsonCloudEventData data = (JsonCloudEventData) cloudEvent.getData();
            client.post(getEndpoint(subscription.getEndpoint())).sendJson(data.getNode())
                    .onComplete(x ->  logger.info(String.format("[CE id = %s] Customer endpoint replied with %d for subscription %s", cloudEvent.getId(), x.result().statusCode(), subscription.getName())));
        }
        logger.info(String.format("[CE id = %s] All subscriptions have been processed", cloudEvent.getId()));
    }

    @Override
    public boolean sendEvent(JsonNode body, String topic) {
        CloudEvent cloudEvent = CloudEventUtils.build(UUID.randomUUID().toString(), topic, URI.create("http://localhost"), JsonNode.class.getName(), "subject", body).get();
        return producer.sendEvent(cloudEvent);
    }

    private WebClient getClient(String url){
        URI uri = URI.create(url);
        boolean sslEnabled = "https".equalsIgnoreCase(uri.getScheme());
        int port = uri.getPort();
        if (port == -1){
            if (sslEnabled){
                port = 443;
            }
            else{
                port = 80;
            }
        }

        return WebClient.create(vertx, new WebClientOptions()
                .setDefaultHost(uri.getHost())
                .setDefaultPort(port)
                .setSsl(sslEnabled)
                .setLogActivity(true));
    }

    private String getEndpoint(String url){
        URI uri = URI.create(url);
        return uri.getRawPath();
    }

}
