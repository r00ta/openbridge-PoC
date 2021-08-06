package com.redhat.developer;

import java.util.concurrent.ExecutionException;

import com.redhat.developer.requests.PublishEventRequest;
import io.smallrye.mutiny.Uni;

public interface EventRegistry {
    Uni<String> getEvents(String groupId);

    Uni<String> getEvent(String groupId, String eventId);

    boolean validate(PublishEventRequest publishEventRequest) throws ExecutionException, InterruptedException;
}
