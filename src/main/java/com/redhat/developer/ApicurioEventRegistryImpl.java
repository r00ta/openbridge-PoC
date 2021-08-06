package com.redhat.developer;

import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.redhat.developer.requests.PublishEventRequest;
import com.redhat.developer.utils.WebClientUtils;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.json.schema.NoSyncValidationException;
import io.vertx.json.schema.Schema;
import io.vertx.json.schema.SchemaParser;
import io.vertx.json.schema.SchemaRouter;
import io.vertx.json.schema.SchemaRouterOptions;
import io.vertx.json.schema.ValidationException;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.context.ThreadContext;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ApicurioEventRegistryImpl implements EventRegistry {
    private final Logger logger = Logger.getLogger(ApicurioEventRegistryImpl.class);

    @ConfigProperty(name="openbridge.apicurio.url")
    String apicurioUrl;

    @Inject
    Vertx vertx;

    @Inject
    io.vertx.core.Vertx validatorVertx;

    WebClient webClient;

    @Inject
    ThreadContext threadContext;

    private SchemaRouter schemaRouter;

    private SchemaParser schemaParser;

    @PostConstruct
    void init(){
        System.out.println("CALLED");
        System.out.println(apicurioUrl);
        webClient = WebClientUtils.getClient(vertx, apicurioUrl);
        schemaRouter = SchemaRouter.create(validatorVertx, new SchemaRouterOptions());
        schemaParser = SchemaParser.createDraft7SchemaParser(schemaRouter);

    }

    @Override
    public Uni<String> getEvents(String groupId) {
        return webClient.get(String.format("/apis/registry/v2/groups/%s/artifacts", groupId)).send().onItem().transform(HttpResponse::bodyAsString);
    }

    @Override
    public Uni<String> getEvent(String groupId, String eventId) {
        return webClient.get(String.format("/apis/registry/v2/groups/%s/artifacts/%s", groupId, eventId)).send().onItem().transform(HttpResponse::bodyAsString);
    }

    @Override
    public boolean validate(PublishEventRequest publishEventRequest) throws ExecutionException, InterruptedException {
        String event = threadContext.withContextCapture(getEvent(publishEventRequest.getEventType().getGroupId(), publishEventRequest.getEventType().getEventId()).subscribeAsCompletionStage()).get();
        logger.info("retrieved json schema " + event);
        Schema schema = schemaParser.parseFromString(event);
        try {
            logger.info(publishEventRequest.getEvent());
            schema.validateSync(new JsonObject(publishEventRequest.getEvent()));
            logger.info("Validation successful");
            // successful validation
            return true;
        } catch (ValidationException e) {
            logger.info(e);
            // Failed validation
        } catch (NoSyncValidationException e) {
            // Cannot validate synchronously. You must validate using validateAsync
        }
        logger.info("Validation failed");
        return false;
    }
}
