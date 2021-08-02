package com.redhat.developer;

import java.util.List;
import java.util.Set;

import com.redhat.developer.models.Subscription;
import com.redhat.developer.models.Topic;
import com.redhat.developer.requests.SubscriptionRequest;
import com.redhat.developer.requests.TopicRequest;

public interface TopicService {

    Topic createTopic(TopicRequest topicRequest);

    Topic getTopic(String topicName);

    List<Topic> getAll();

    Subscription createSubscription(String topicName, SubscriptionRequest subscriptionRequest);

    Set<Subscription> getSubscriptions(String topicName);
}
