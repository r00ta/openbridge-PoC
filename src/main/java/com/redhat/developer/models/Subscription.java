package com.redhat.developer.models;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.developer.models.filters.Filter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

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

    @Column(name = "transformationTemplate", nullable = true)
    @JsonProperty("transformationTemplate")
    private String transformationTemplate;

    @Transient
    private Set<Filter> filters;

    // A little hack for nice api response.... jpa does not support polymorphism with abstract classes => filters.
    // I store the serialization of the filters here and when `getFilters` is called I deserialize it back. Sorry for that :)
    @LazyCollection(LazyCollectionOption.FALSE)
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "filters", nullable = true)
    @JsonIgnore
    private Set<String> serializedFilters;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "filtersTemplates", nullable = true)
    @JsonIgnore
    private Set<String> filtersTemplates;

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

    public void setTransformationTemplate(String transformationTemplate) {
        this.transformationTemplate = transformationTemplate;
    }

    public void setSerializedFilters(Set<String> serializedFilters) {
        this.serializedFilters = serializedFilters;
    }

    public void setFiltersTemplates(Set<String> filtersTemplates) {
        this.filtersTemplates = filtersTemplates;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getName() {
        return name;
    }

    public String getTransformationTemplate() {
        return transformationTemplate;
    }

    public Set<String> getSerializedFilters() {
        return serializedFilters;
    }

    public Set<String> getFiltersTemplates() {
        return filtersTemplates;
    }

    public void setFilters(Set<Filter> filters) {
        this.filters = filters;
    }

    @JsonProperty("filters")
    public Set<Filter> getFilters() {
        if (serializedFilters == null) {
            return null;
        }
        return serializedFilters.stream().map(x -> {
            try {
                return new ObjectMapper().readValue(x, Filter.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toSet());
    }
}
