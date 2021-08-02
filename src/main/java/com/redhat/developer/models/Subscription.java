package com.redhat.developer.models;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "SUBSCRIPTION")
public class Subscription {

    @Id
    @Column(name = "name")
    @JsonProperty("name")
    private String name;

    @Column(name = "endpoint")
    @JsonProperty("endpoint")
    private String endpoint;

    @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Topic topic;

    public Subscription(){}

    public void setName(String name){
        this.name = name;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getName() {
        return name;
    }
}
