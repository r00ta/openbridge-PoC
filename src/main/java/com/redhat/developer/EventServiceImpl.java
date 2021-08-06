package com.redhat.developer;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.redhat.developer.models.RegistryEvent;
import com.redhat.developer.models.Subscription;
import com.redhat.developer.models.Topic;
import com.redhat.developer.producer.EventProducer;
import com.redhat.developer.utils.CloudEventUtils;
import com.redhat.developer.utils.WebClientUtils;
import io.cloudevents.CloudEvent;
import io.cloudevents.jackson.JsonCloudEventData;
import io.quarkus.qute.Engine;
import io.quarkus.qute.Template;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import org.eclipse.microprofile.context.ThreadContext;
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

    @Inject
    ThreadContext threadContext;

    @Override
    public void process(CloudEvent cloudEvent){
        logger.info(String.format("[CE id = %s] Start processing", cloudEvent.getId()));
        Topic topic = topicService.getTopic(cloudEvent.getExtension("topic").toString());
        String groupId = cloudEvent.getExtension("mygroupid").toString();
        String eventId = cloudEvent.getExtension("myeventid").toString();

        logger.info(String.format("[CE id = %s] There are %d available subscriptions", cloudEvent.getId(), topic.getSubscriptions().size()));
        for (Subscription subscription : topic.getSubscriptions()){
            logger.info(String.format("[CE id = %s] Processing subscription %s with endpoint %s", cloudEvent.getId(), subscription.getName(), subscription.getEndpoint()));
            WebClient client = WebClientUtils.getClient(vertx, subscription.getEndpoint());
            JsonCloudEventData data = (JsonCloudEventData) cloudEvent.getData();
            JsonNode dataToSend = data.getNode();
            Map<String, Object> originalMap = CloudEventUtils.Mapper.mapper().convertValue(data.getNode(), new TypeReference<Map<String, Object>>(){});

            if (!subscription.getRegistryEvents().isEmpty()){
                if (subscription.getRegistryEvents().stream().noneMatch(x -> x.getGroupId().equals(groupId) && x.getEventId().equals(eventId))){
                    logger.info(String.format("[CE id = %s] Subscription %s does not match event type", cloudEvent.getId(), subscription.getName()));
                    continue;
                }
            }

            if (!checkFilters(subscription, originalMap)){
                logger.info(String.format("[CE id = %s] Subscription %s does not match filters", cloudEvent.getId(), subscription.getName()));
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
            logger.info(String.format("[CE id = %s] Sending webhook", cloudEvent.getId()));
            threadContext.withContextCapture(client.post(
                    WebClientUtils.getEndpoint(subscription.getEndpoint()))
                    .sendJson(dataToSend).onItem().invoke(x -> logger.info(String.format("[CE id = %s] Sending webhook - status code %d", cloudEvent.getId(), x.statusCode()))).subscribeAsCompletionStage());
        }
        logger.info(String.format("[CE id = %s] All subscriptions have been processed", cloudEvent.getId()));
    }

    @Override
    public boolean sendEvent(JsonNode body, String topic, RegistryEvent registryEvent) {
        CloudEvent cloudEvent = CloudEventUtils.build(UUID.randomUUID().toString(), topic, registryEvent,
                                                      URI.create("http://localhost"), JsonNode.class.getName(), "subject", body).get();
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
}
