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
import org.springframework.beans.factory.annotation.Autowired;


@GrpcService
public class MicrosubService extends MicrosubGrpc.MicrosubImplBase {

    @Autowired
    private TwitchApiProvider twitchApiProvider;

    @Autowired
    private ConsumerService consumerService;

    @Autowired
    private ConfigProperties config;

    @Autowired
    private EventsubService eventsubService;

    @Override
    public void register(Registration registration, StreamObserver<Empty> responseObserver) {
        Consumer consumer = consumerService.findByCallback(registration.getCallback());

        if (consumer == null) {
            consumer = new Consumer(registration.getCallback());
            this.consumerService.save(consumer);
        }

        this.twitchApiProvider.registerEventsub("channel.follow", registration.getId(), consumer);

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

        // TODO: delete dangling consumers, preferably via sql directly (cascade)

        Empty response = Empty.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
