package com.redhat.developer.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import com.redhat.developer.models.Subscription;
import com.redhat.developer.models.Topic;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

@ApplicationScoped
@Transactional
public class TopicDAO implements PanacheRepositoryBase<Topic, String> {

}
