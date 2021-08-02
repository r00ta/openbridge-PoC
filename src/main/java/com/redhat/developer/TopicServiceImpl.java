package com.redhat.developer;

import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.redhat.developer.dao.SubscriptionDAO;
import com.redhat.developer.dao.TopicDAO;
import com.redhat.developer.models.Subscription;
import com.redhat.developer.models.Topic;
import com.redhat.developer.requests.SubscriptionRequest;
import com.redhat.developer.requests.TopicRequest;

@ApplicationScoped
public class TopicServiceImpl implements TopicService {

    @Inject
    TopicDAO topicDAO;

    @Inject
    SubscriptionDAO subscriptionDAO;

    @Override
    @Transactional
    public Topic createTopic(TopicRequest topicRequest) {
        Topic topic = topicRequest.toEntity();
        topicDAO.persist(topic);
        return topic;
    }

    @Override
    @Transactional
    public Topic getTopic(String name) {
        return topicDAO.findById(name);
    }

    @Override
    @Transactional
    public List<Topic> getAll() {
        return topicDAO.listAll();
    }

    @Override
    @Transactional
    public Subscription createSubscription(String topicName, SubscriptionRequest subscriptionRequest) {
        Subscription subscription = subscriptionRequest.toEntity();
        Topic topic = topicDAO.findById(topicName);

        subscription.setTopic(topic);

        topic.getSubscriptions().add(subscription);

        subscriptionDAO.persist(subscription);
        topicDAO.getEntityManager().merge(topic);
        return subscription;
    }

    @Override
    @Transactional
    public Set<Subscription> getSubscriptions(String topicName) {
        Topic topic = topicDAO.findById(topicName);
        return topic.getSubscriptions();
    }
}
