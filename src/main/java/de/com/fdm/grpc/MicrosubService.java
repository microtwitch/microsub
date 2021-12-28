package de.com.fdm.grpc;

import de.com.fdm.config.ConfigProperties;
import de.com.fdm.grpc.microsub.lib.Deletion;
import de.com.fdm.grpc.microsub.lib.Empty;
import de.com.fdm.grpc.microsub.lib.MicrosubGrpc;
import de.com.fdm.grpc.microsub.lib.Registration;
import de.com.fdm.mongo.Consumer;
import de.com.fdm.mongo.ConsumerRepository;
import de.com.fdm.mongo.EventsubRepository;
import de.com.fdm.twitch.EventsubEntity;
import de.com.fdm.twitch.TwitchApiProvider;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@GrpcService
public class MicrosubService extends MicrosubGrpc.MicrosubImplBase {

    @Autowired
    private TwitchApiProvider twitchApiProvider;

    @Autowired
    private ConsumerRepository consumerRepository;

    @Autowired
    private EventsubRepository eventsubRepository;

    @Autowired
    private ConfigProperties config;

    @Override
    public void register(Registration registration, StreamObserver<Empty> responseObserver) {
        List<EventsubEntity> eventsubEntities = this.eventsubRepository.findAll();

        for (EventsubEntity eventsub : eventsubEntities) {
            if (eventsub.getData().get(0).getCondition().getBroadcaster_user_id().equals(registration.getId())) {
                consumerRepository.save(new Consumer(registration.getCallback(), registration.getId()));

                Empty response = Empty.newBuilder().build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }
        }

        this.twitchApiProvider.registerEventsub("channel.follow", registration.getId(), config.getUrl() + "follow");

        consumerRepository.save(new Consumer(registration.getCallback(), registration.getId()));

        Empty response = Empty.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void delete(Deletion deletion, StreamObserver<Empty> responseObserver) {
        List<Consumer> consumerList = this.consumerRepository.findAll();

        for (Consumer consumer : consumerList) {
            if (consumer.getBroadcasterUserId().equals(deletion.getId()) && consumer.getCallback().equals(deletion.getCallback())) {
                this.consumerRepository.deleteById(consumer.get_id().toString());
            }
        }
        // check if eventsubentity is still needed
        consumerList = this.consumerRepository.findAll();
        for (Consumer consumer : consumerList) {
            if (consumer.getBroadcasterUserId().equals(deletion.getId())) {
                Empty response = Empty.newBuilder().build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }
        }
        this.twitchApiProvider.deleteEventsub(deletion.getId());

        List<EventsubEntity> eventsubEntities = this.eventsubRepository.findAll();
        for (EventsubEntity eventsub: eventsubEntities) {
            if (eventsub.getData().get(0).getCondition().getBroadcaster_user_id().equals(deletion.getId())) {
                this.eventsubRepository.deleteById(eventsub.get_id().toString());
            }
        }


        Empty response = Empty.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
