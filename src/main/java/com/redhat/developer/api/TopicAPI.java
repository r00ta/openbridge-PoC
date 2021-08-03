package com.redhat.developer.api;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
import com.redhat.developer.CloudEventUtils;
import com.redhat.developer.EventService;
import com.redhat.developer.TopicService;
import com.redhat.developer.models.Subscription;
import com.redhat.developer.models.Topic;
import com.redhat.developer.producer.EventProducer;
import com.redhat.developer.requests.SubscriptionRequest;
import com.redhat.developer.requests.TopicRequest;
import io.cloudevents.CloudEvent;

@Path("/topics")
public class TopicAPI {

    @Inject
    TopicService topicService;

    @Inject
    EventService eventService;


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
    public Response publicEvent(@QueryParam("topicName") String topicName, String body) throws JsonProcessingException {
        JsonNode jsonNode = new ObjectMapper().readTree(body);
        boolean success = eventService.sendEvent(jsonNode, topicName);
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
