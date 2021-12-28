package de.com.fdm.grpc;

import de.com.fdm.grpc.microsub.lib.Empty;
import io.grpc.stub.StreamObserver;

public class MicrosubCallback implements StreamObserver<Empty> {
    @Override
    public void onNext(Empty value) {

    }

    @Override
    public void onError(Throwable t) {
        System.out.println(t.getMessage());
    }

    @Override
    public void onCompleted() {

    }
}
