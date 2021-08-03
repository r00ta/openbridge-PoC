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
with the endpoint POST `/topic/{topicName}/subscriptions`. I'd suggest to use the website `https://webhook.site/` to create tmp webhooks that you can use in the subscription. 

At the end, you can publish some JSON events with the endpoint POST `/topic/{topicName}` and check that you have received them in the applications you subscribed.
