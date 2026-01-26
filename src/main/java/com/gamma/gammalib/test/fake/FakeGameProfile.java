package com.gamma.gammalib.test.fake;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import com.mojang.authlib.GameProfile;

public class FakeGameProfile extends GameProfile {

    private static final AtomicInteger counter = new AtomicInteger(0);

    public static void cleanup() {
        counter.set(0);
    }

    public FakeGameProfile(UUID id, String name) {
        super(id, name);
    }

    public FakeGameProfile(UUID id) {
        super(id, "Fake-Player-" + counter.getAndIncrement());
    }
}
