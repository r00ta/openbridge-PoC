package com.redhat.developer.models.filters;

import javax.persistence.Embeddable;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@Embeddable
@MappedSuperclass
@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = Filter.FILTER_TYPE_FIELD)
@JsonSubTypes({
        @JsonSubTypes.Type(value = NumberGreaterThanOrEquals.class, name = NumberGreaterThanOrEquals.FILTER_TYPE_NAME),
        @JsonSubTypes.Type(value = NumberIn.class, name = NumberIn.FILTER_TYPE_NAME),
        @JsonSubTypes.Type(value = NumberInRange.class, name = NumberInRange.FILTER_TYPE_NAME),
        @JsonSubTypes.Type(value = NumberLessThan.class, name = NumberLessThan.FILTER_TYPE_NAME),
        @JsonSubTypes.Type(value = NumberLessThanOrEquals.class, name = NumberLessThanOrEquals.FILTER_TYPE_NAME),
        @JsonSubTypes.Type(value = NumberNotIn.class, name = NumberNotIn.FILTER_TYPE_NAME),
        @JsonSubTypes.Type(value = NumberNotInRange.class, name = NumberNotInRange.FILTER_TYPE_NAME),
        @JsonSubTypes.Type(value = StringBeginsWith.class, name = StringBeginsWith.FILTER_TYPE_NAME),
        @JsonSubTypes.Type(value = StringContains.class, name = StringContains.FILTER_TYPE_NAME),
        @JsonSubTypes.Type(value = StringEndsWith.class, name = StringEndsWith.FILTER_TYPE_NAME),
        @JsonSubTypes.Type(value = StringIn.class, name = StringIn.FILTER_TYPE_NAME),
        @JsonSubTypes.Type(value = StringEquals.class, name = StringEquals.FILTER_TYPE_NAME),
        @JsonSubTypes.Type(value = StringNotBeginsWith.class, name = StringNotBeginsWith.FILTER_TYPE_NAME),
        @JsonSubTypes.Type(value = StringNotContains.class, name = StringNotContains.FILTER_TYPE_NAME),
        @JsonSubTypes.Type(value = StringNotEndsWith.class, name = StringNotEndsWith.FILTER_TYPE_NAME),
        @JsonSubTypes.Type(value = StringNotIn.class, name = StringNotIn.FILTER_TYPE_NAME),
        @JsonSubTypes.Type(value = BoolEquals.class, name = BoolEquals.FILTER_TYPE_NAME)
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Filter {
    public static final String FILTER_TYPE_FIELD = "type";

    @JsonProperty(FILTER_TYPE_FIELD)
    protected String type;

    @JsonProperty("key")
    protected String key;

    @JsonIgnore
    public String getConditionTemplate(){
        return null;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
