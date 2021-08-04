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
- Filter: you can filter the events using some operators like `CONTAINS`, `NOT_CONTAINS`, `IN`, `NOT_IN`, `EQUALS`. For example with the following subscription
```json
{
  "endpoint": "https://webhook.site/ce12f81e-9eff-4a26-83ee-17c65bc25d1c",
  "name": "prova1",
  "filters": [
    {
      "type": "CONTAINS",
      "key": "name",
      "value": "opo"
    }
  ]
}
```
you will receive only those events that contain the string `opo` in the `name` key. You can navigate in the object with the dot notation. For example with

```json 
{
  "endpoint": "https://webhook.site/ce12f81e-9eff-4a26-83ee-17c65bc25d1c",
  "name": "prova1",
  "transformationTemplate": "{\"{name}\": \"{surename}\"}",
  "filters": [
    {
      "type": "IN",
      "key": "data.name",
      "value": "[\"jacopo\", \"marco\"]"
    }
  ]
}
```

you will receive only those events that contains `jacopo` or `marco` in the object `data.list` (for example this is a valid event that you would receive`{"data": {"name": "jacopo"}}`).
The syntax for the other operands is trivial and left to the imagination of the reader :) 

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
      "type": "CONTAINS",
      "key": "name",
      "value": "opo"
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


