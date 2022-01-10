package de.com.fdm.grpc;

import de.com.fdm.config.ConfigProperties;
import de.com.fdm.grpc.microsub.lib.Deletion;
import de.com.fdm.grpc.microsub.lib.Empty;
import de.com.fdm.grpc.microsub.lib.MicrosubGrpc;
import de.com.fdm.grpc.microsub.lib.Registration;
import de.com.fdm.db.data.Consumer;
import de.com.fdm.db.data.Eventsub;
import de.com.fdm.db.services.ConsumerService;
import de.com.fdm.db.services.EventsubService;
import de.com.fdm.twitch.TwitchApiProvider;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


@GrpcService
public class MicrosubService extends MicrosubGrpc.MicrosubImplBase {
    Logger logger = LoggerFactory.getLogger(MicrosubService.class);

    @Autowired
    private TwitchApiProvider twitchApiProvider;

    @Autowired
    private ConsumerService consumerService;

    @Autowired
    private EventsubService eventsubService;

    @Override
    public void register(Registration registration, StreamObserver<Empty> responseObserver) {
        Consumer consumer = consumerService.findByCallback(registration.getCallback());

        if (consumer == null) {
            consumer = consumerService.save(new Consumer(registration.getCallback()));
        }

        for (Eventsub eventsub : consumer.getEventsubs()) {
            if (eventsub.getType() == registration.getType() && eventsub.getBroadcasterUserId().equals(registration.getId())) {
                Empty response = Empty.newBuilder().build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                logger.info("Eventsub already registered: callback={}, id={}, type={}", registration.getCallback(), registration.getId(), registration.getType());
                return;
            }
        }

        Eventsub eventsub = this.twitchApiProvider.registerEventsub(registration.getType(), registration.getId(), consumer);
        consumer.addEventsub(eventsub);
        consumerService.save(consumer);

        Empty response = Empty.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void delete(Deletion deletion, StreamObserver<Empty> responseObserver) {

        // TODO: change id to condition_id or similar to make intention clear
        this.eventsubService.deleteConsumer(deletion.getId(), deletion.getCallback());

        Eventsub eventsub = this.eventsubService.findByConditionId(deletion.getId());
        if (eventsub == null) {
            Empty response = Empty.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }
        if (!eventsub.hasConsumers()) {
            this.twitchApiProvider.deleteEventsub(eventsub.getTwitchId());
            this.eventsubService.delete(eventsub);
        }

        for (Consumer consumer : consumerService.findAll()) {
            if (consumer.getEventsubs().size() == 0) {
                consumerService.delete(consumer);
            }
        }

        Empty response = Empty.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
