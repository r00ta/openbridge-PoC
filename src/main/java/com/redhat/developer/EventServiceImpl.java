package com.redhat.developer;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.redhat.developer.models.filters.Filter;
import com.redhat.developer.models.Subscription;
import com.redhat.developer.models.Topic;
import com.redhat.developer.producer.EventProducer;
import io.cloudevents.CloudEvent;
import io.cloudevents.jackson.JsonCloudEventData;
import io.quarkus.qute.Engine;
import io.quarkus.qute.Template;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import org.jboss.logging.Logger;

@ApplicationScoped
public class EventServiceImpl implements EventService {
    private final Logger logger = Logger.getLogger(EventServiceImpl.class);

    private static final Engine engine = Engine.builder().addDefaults().build();

    @Inject
    EventProducer producer;

    @Inject
    TopicService topicService;

    @Inject
    FEELEvaluator feelEvaluator;

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
            JsonNode dataToSend = data.getNode();
            Map<String, Object> originalMap = CloudEventUtils.Mapper.mapper().convertValue(data.getNode(), new TypeReference<Map<String, Object>>(){});

            if (!checkFilters(subscription, originalMap)){
                logger.info("Filter does not match");
                continue;
            }

            if (subscription.getTransformationTemplate() != null && !subscription.getTransformationTemplate().equals("")){
                Template helloTemplate = engine.parse(subscription.getTransformationTemplate());
                try {
                    dataToSend = CloudEventUtils.Mapper.mapper().readTree(helloTemplate.data(originalMap).render());
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                logger.info(dataToSend);
            }
            client.post(
                    getEndpoint(subscription.getEndpoint()))
                    .sendJson(dataToSend)
                    .onComplete(x ->  logger.info(String.format("[CE id = %s] Customer endpoint replied with %d for subscription %s", cloudEvent.getId(), x.result().statusCode(), subscription.getName())));
        }
        logger.info(String.format("[CE id = %s] All subscriptions have been processed", cloudEvent.getId()));
    }

    @Override
    public boolean sendEvent(JsonNode body, String topic) {
        CloudEvent cloudEvent = CloudEventUtils.build(UUID.randomUUID().toString(), topic, URI.create("http://localhost"), JsonNode.class.getName(), "subject", body).get();
        return producer.sendEvent(cloudEvent);
    }

    private boolean checkFilters(Subscription subscription, Map<String, Object> data){
        if (subscription.getFiltersTemplates() == null){
            return true;
        }
        for (String filter : subscription.getFiltersTemplates()){
            if (!feelEvaluator.evaluateFilter(filter, data)){
                return false;
            }
        }
        return true;
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
