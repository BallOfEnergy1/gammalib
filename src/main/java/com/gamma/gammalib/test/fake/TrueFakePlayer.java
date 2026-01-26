package com.gamma.gammalib.test.fake;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldServer;

public class TrueFakePlayer extends EntityPlayerMP {

    private static final MinecraftServer SERVER = MinecraftServer.getServer();

    private final PathFinder pathFinder;
    private final PlayerMoveHelper moveHelper;
    private final PlayerNavigate navigator;

    private PathEntity pendingPath;

    public TrueFakePlayer(WorldServer world) {
        super(SERVER, world, new FakeGameProfile(UUID.randomUUID()), new ItemInWorldManager(world));

        pathFinder = new PathFinder(world, false, false, true, false);
        moveHelper = new PlayerMoveHelper(this);
        navigator = new PlayerNavigate(this, world);
    }

    public PlayerMoveHelper getMoveHelper() {
        return moveHelper;
    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();

        navigator.onUpdateNavigation();
        moveHelper.onUpdateMoveHelper();
    }

    public void precalculateMoveTo(int x, int z) {
        int y = worldObj.getHeightValue(x, z);
        pendingPath = pathFinder.createEntityPathTo(this, x, y, z, Float.MAX_VALUE);
    }

    public void begin() {
        navigator.setPath(pendingPath);
    }

    public void kickPlayerFromServer(String reason) {

    }

    public void onDisconnect(IChatComponent reason) {

    }
}
