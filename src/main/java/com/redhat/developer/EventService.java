package com.redhat.developer;

import com.fasterxml.jackson.databind.JsonNode;
import io.cloudevents.CloudEvent;

public interface EventService {

    void process(CloudEvent cloudEvent);

    boolean sendEvent(JsonNode body, String topic);

}
