package com.redhat.developer.producer;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.redhat.developer.utils.CloudEventUtils;
import io.cloudevents.CloudEvent;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

@ApplicationScoped
public class EventProducer {
    private final Logger logger = Logger.getLogger(EventProducer.class);

    @Inject
    @Channel("events-out")
    Emitter<String> emitter;

    public boolean sendEvent(CloudEvent cloudEvent) {
        logger.info("Sending event to event queue");
        Optional<String> serializedCloudEvent = CloudEventUtils.encode(cloudEvent);
        if (serializedCloudEvent.isEmpty()){
            logger.info("Sending event to event queue - FAILED TO SERIALIZE");
            return false;
        }
        emitter.send(serializedCloudEvent.get());
        logger.info("Sending event to event queue - SUCCESS");
        return true;
    }
}
