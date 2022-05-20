package de.com.fdm.twitch.data;

import java.util.List;

public record EventSub(List<Data> data) {
    public record Data(String id) {}
}
