package com.redhat.developer.api;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.redhat.developer.EventRegistry;
import io.smallrye.mutiny.Uni;

@Path("/registry")
public class EventRegistryAPI {

    @Inject
    EventRegistry eventRegistry;

    @GET
    @Path("/{groupId}/events")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<String> getEvents(@PathParam("groupId") String groupId){
        Uni<String> events = eventRegistry.getEvents(groupId);
        return events;
    }

    @GET
    @Path("/{groupId}/events/{eventId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<String> getEvent(@PathParam("groupId") String groupId, @PathParam("eventId") String eventId){
        Uni<String> event = eventRegistry.getEvent(groupId, eventId);
        return event;
    }
}
