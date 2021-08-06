package com.redhat.developer;

import com.fasterxml.jackson.databind.JsonNode;
import com.redhat.developer.models.RegistryEvent;
import io.cloudevents.CloudEvent;

public interface EventService {

    void process(CloudEvent cloudEvent);

    boolean sendEvent(JsonNode body, String topic, RegistryEvent registryEvent);

}
