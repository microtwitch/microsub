package de.com.fdm.grpc;

import de.com.fdm.grpc.microsub.lib.ConsumerGrpc;
import de.com.fdm.grpc.microsub.lib.EventsubMessage;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class MicrosubClient {
    private final ConsumerGrpc.ConsumerStub asyncStub;

    public MicrosubClient(String target) {
        this(ManagedChannelBuilder.forTarget(target)
                .usePlaintext());
    }

    private MicrosubClient(ManagedChannelBuilder<?> channelBuilder) {
        ManagedChannel channel = channelBuilder.build();
        this.asyncStub = ConsumerGrpc.newStub(channel);
    }

    public void send(EventsubMessage message) {
        this.asyncStub.consume(message, new MicrosubCallback());
    }
}
