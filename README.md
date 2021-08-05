# OpenBridge proof of concept

## Architecture 

High level architecture

![ob](https://user-images.githubusercontent.com/18282531/127902215-8c57f61d-c210-4560-acc0-3fe455d2e366.png)

## Development mode

In order to start playing with this PoC you have to start docker compose with 

```bash
docker-compose up
```

and then run this quarkus application with 

```bash
mvn clean compile quarkus:dev
```

You can then access the swagger-ui at `http://localhost:8080/q/swagger-ui` and create a topic with the POST `/topic` endpoint. You can then create a subscription for a topic 
with the endpoint POST `/topic/{topicName}/subscriptions`. I'd suggest to use the website `https://webhook.site/` to create tmp webhooks that you can use in the subscription. With this endpoint you have the possibility to specify how to transform the original event with a qute template. 
At the end, you can publish some JSON events with the endpoint POST `/topic/{topicName}/events` and check that you have received them in the applications you subscribed. The templating is optional, if you omit it, you will get the "original" event as it is

## Templating

With the endpoint POST `/topic/{topicName}/subscriptions` you can create subscriptions and perform two kind of operations before eventually receive the event to your endpoint: 
- Filter: you can filter the events using some operators like `NumberIn`, `NumberInRange`, `NumberLessThan`, `NumberGreatherThanOrEquals`, `NumberLessThan`, `NumberLessThanOrEquals`, `NumberNotIn`, `NumberNotInRange`, `StringBeginsWith`, `StringContains`, `StringEndsWith`, `StringEquals`, `StringIn`, `StringNotBeginsWith`, `StringNotContains`, `StringNotEndsWith`, `StringNotIn`, `BoolEquals`. 
  For example with the following subscription. Documentation for each filter is TO BE DONE. 
  If you specify multiple filters, they are ANDed. You can only specify OR conditions within the same filter: for example with the following request.
```json
{
  "endpoint": "https://webhook.site/ce12f81e-9eff-4a26-83ee-17c65bc25d1c",
  "name": "prova1",
  "filters": [
    {
      "type": "StringContains",
      "key": "name",
      "values": ["opo", "arco"]
    }
  ]
}
```
you will receive only those events that contain the string `opo` OR `arco` in the `name` key. 

For those operators that accept only one parameter, use `value` instead of `values` for example: 
```json
{
  "endpoint": "https://webhook.site/ce12f81e-9eff-4a26-83ee-17c65bc25d1c",
  "name": "prova1",
  "filters": [
    {
      "type": "StringEquals",
      "key": "name",
      "value": "jacopo"
    }
  ]
}
```

You can navigate in the object with the dot notation. For example with

```json 
{
  "endpoint": "https://webhook.site/ce12f81e-9eff-4a26-83ee-17c65bc25d1c",
  "name": "prova1",
  "filters": [
    {
      "type": "NumberNotInRange",
      "key": "data.age",
      "value": "[[18,30], [60, 80]]"
    }
  ]
}
```

you will receive only those events that contains `data.age` within `18` and `30` OR between `60` and `80` (for example this is a valid event that you would receive`{"data": {"age": 20}}`).

- Templating: you can specify a new template to transform the original event to a new json. For example the following subscription 
```json
{
  "endpoint": "https://webhook.site/ce12f81e-9eff-4a26-83ee-17c65bc25d1c",
  "name": "prova1",
  "transformationTemplate": "{\"{name}\": \"{surename}\"}"
}
```
would transform the event `{"name": "jacopo", "surename": "rota"}` into `{"jacopo": "rota"}`. You can always navigate in the objects with the dot notation like for the filtering. 
*NOTE: the filtering is applied to the properties and the values of the original event.*

## Example

Create a topic using the endpoint POST `/topic` with the body 

```json
{
  "name": "test"
}
```

create a subscription with the endpoint POST `/topic/test/subscriptions` and the body

```json
{
  "endpoint": "https://webhook.site/ce12f81e-9eff-4a26-83ee-17c65bc25d1c",
  "name": "prova1",
  "transformationTemplate": "{\"{name}\": \"{surename}\"}",
  "filters": [
    {
      "type": "StringContains",
      "key": "name",
      "value": ["opo"]
    }
  ]
}
``` 

and then send the event with the endpoint POST `/topic/test/events` and the body

```json 
{"name": "jacopo", "surename": "rota"}
```

the event received by the webhook would look like 

```json
{"jacopo": "rota"}
```


