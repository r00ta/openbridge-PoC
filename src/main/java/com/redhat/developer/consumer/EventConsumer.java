package com.redhat.developer.consumer;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.redhat.developer.CloudEventUtils;
import com.redhat.developer.EventService;
import io.cloudevents.CloudEvent;
import io.smallrye.reactive.messaging.annotations.Blocking;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

@ApplicationScoped
public class EventConsumer {
    private final Logger logger = Logger.getLogger(EventConsumer.class);

    @Inject
    EventService eventService;

    @Blocking
    @Incoming("events-in")
    public CompletionStage<Void> consume(Message<String> message){
//        try{
            logger.info("New event to be processed by event consumer");
            Optional<CloudEvent> cloudEvent = CloudEventUtils.decode(message.getPayload());
            if (cloudEvent.isEmpty()){
                logger.warn("An empty cloud event has been received, sending nack");
                return message.nack(new RuntimeException("Could not deserialize cloudevent"));
            }
            eventService.process(cloudEvent.get());
            logger.info("New event to be processed by event consumer - COMPLETED");
            return message.ack();
//        }
//        catch (Exception e){
//            logger.warn("Exception " + e);
//            return message.ack();
//        }
    }
}
