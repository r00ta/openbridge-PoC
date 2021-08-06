package com.redhat.developer.api;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.developer.EventRegistry;
import com.redhat.developer.EventService;
import com.redhat.developer.TopicService;
import com.redhat.developer.models.Subscription;
import com.redhat.developer.models.Topic;
import com.redhat.developer.requests.PublishEventRequest;
import com.redhat.developer.requests.SubscriptionRequest;
import com.redhat.developer.requests.TopicRequest;
import org.jboss.logging.Logger;

@Path("/topics")
public class TopicAPI {

    private final Logger logger = Logger.getLogger(TopicAPI.class);

    @Inject
    TopicService topicService;

    @Inject
    EventService eventService;

    @Inject
    EventRegistry eventRegistry;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTopics(){
        List<Topic> topics = topicService.getAll();
        return Response.ok(topics).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createTopic(TopicRequest topicRequest){
        Topic topic = topicService.createTopic(topicRequest);
        return Response.ok(topic).build();
    }

    @POST
    @Path("/{topicName}/events")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response publishEvent(@QueryParam("topicName") String topicName, PublishEventRequest publishEventRequest) throws JsonProcessingException, ExecutionException, InterruptedException {
        if (publishEventRequest.getEventType() == null){
            return Response.status(400, "Event type must be registered on the event registry and must be included in the api request.").build();
        }

        if (!eventRegistry.validate(publishEventRequest)){
            logger.error("Event is not valid " + publishEventRequest.toString());
            return Response.status(400, "The data you sent is not compatible with the event type in the event registry").build();
        }

        JsonNode jsonNode = new ObjectMapper().readTree(publishEventRequest.getEvent());
        boolean success = eventService.sendEvent(jsonNode, topicName, publishEventRequest.getEventType());
        if (success){
            return Response.accepted().build();
        }
        else{
            return Response.status(400).build();
        }
    }

    @DELETE
    @Path("/{topicName}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteTopic(@QueryParam("topicName") String topicName){
        if (topicName.equals("default")){
            return Response.status(400, "default topic is reserved.").build();
        }
        boolean success = topicService.deleteTopic(topicName);
        if (success){
            return Response.accepted().build();
        }
        else{
            return Response.status(400).build();
        }
    }

    @GET
    @Path("/{topicName}/subscriptions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubscriptions(@QueryParam("topicName") String topicName){
        Set<Subscription> subscriptions = topicService.getSubscriptions(topicName);
        return Response.ok(subscriptions).build();
    }

    @POST
    @Path("/{topicName}/subscriptions")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createSubscription(@PathParam("topicName") String topicName, SubscriptionRequest subscriptionRequest){
        Subscription subscription = topicService.createSubscription(topicName, subscriptionRequest);
        return Response.ok(subscription).build();
    }

    @DELETE
    @Path("/{topicName}/subscriptions/{subscriptionName}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteSubscription(@PathParam("topicName") String topicName, @PathParam("subscriptionName") String subscriptionName){
        boolean success = topicService.deleteSubscription(topicName, subscriptionName);
        if (success){
            return Response.accepted().build();
        }
        else{
            return Response.status(400).build();
        }
    }
}
