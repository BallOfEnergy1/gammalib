package com.gamma.gammalib.test;

import java.util.List;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gamma.gammalib.test.fake.FakeNetHandlerPlayServer;
import com.gamma.gammalib.test.fake.TrueFakePlayer;
import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.FMLCommonHandler;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class WorldTestHandler {

    private static final Logger logger = LogManager.getLogger("Spool-WorldTestHandler");

    private final WorldServer world;
    private final int count;
    private final int radius;

    List<TrueFakePlayer> fakePlayers = new ObjectArrayList<>();

    WorldTestHandler(WorldServer world, int count, int radius) {
        this.world = world;
        this.count = count;
        this.radius = radius;
    }

    /**
     * Initialize players/generate coordinate circle and precalculate movement.
     */
    public void stage1() {
        for (int idx = 0; idx < count; idx++) {
            TrueFakePlayer tfPlayer = new TrueFakePlayer(world);
            fakePlayers.add(tfPlayer);
        }

        final double TAU = Math.PI * 2;
        double stepSize = TAU / count;
        int[][] coordinates = new int[count][2];

        int i = 0;
        for (double angle = 0; angle < TAU; angle += stepSize) {
            coordinates[i][0] = (int) (radius * Math.cos(angle));
            coordinates[i][1] = (int) (radius * Math.sin(angle));
            i++;
        }

        for (int idx = 0; idx < count; idx++) {
            TrueFakePlayer tfPlayer = fakePlayers.get(idx);
            tfPlayer.precalculateMoveTo(coordinates[idx][0], coordinates[idx][1]);
        }
    }

    /**
     * Simulate all players joining server.
     */
    public void stage2() {
        MinecraftServer server = MinecraftServer.getServer();
        ServerConfigurationManager scm = server.getConfigurationManager();
        for (TrueFakePlayer tfPlayer : fakePlayers) {
            GameProfile gameprofile = tfPlayer.getGameProfile();
            PlayerProfileCache playerprofilecache = server.func_152358_ax();
            GameProfile gameprofile1 = playerprofilecache.func_152652_a(gameprofile.getId());
            String s = gameprofile1 == null ? gameprofile.getName() : gameprofile1.getName();
            playerprofilecache.func_152649_a(gameprofile);

            tfPlayer.dimension = 0;
            World playerWorld = tfPlayer.worldObj = this.world;
            ChunkCoordinates spawnPoint = playerWorld.provider.getRandomizedSpawnPoint();
            tfPlayer.setPosition(spawnPoint.posX, spawnPoint.posY, spawnPoint.posZ);

            tfPlayer.setWorld(playerWorld);
            tfPlayer.theItemInWorldManager.setWorld((WorldServer) tfPlayer.worldObj);
            String s1 = "local";

            logger.info(
                tfPlayer.getCommandSenderName() + "["
                    + s1
                    + "] logged in with entity id "
                    + tfPlayer.getEntityId()
                    + " at ("
                    + tfPlayer.posX
                    + ", "
                    + tfPlayer.posY
                    + ", "
                    + tfPlayer.posZ
                    + ")");
            WorldServer worldserver = server.worldServerForDimension(tfPlayer.dimension);
            tfPlayer.theItemInWorldManager.setGameType(WorldSettings.GameType.CREATIVE);
            tfPlayer.playerNetServerHandler = new FakeNetHandlerPlayServer(server, tfPlayer);
            tfPlayer.func_147099_x()
                .func_150877_d();
            tfPlayer.func_147099_x()
                .func_150884_b(tfPlayer);
            server.func_147132_au();
            ChatComponentTranslation chatcomponenttranslation;

            if (!tfPlayer.getCommandSenderName()
                .equalsIgnoreCase(s)) {
                chatcomponenttranslation = new ChatComponentTranslation(
                    "multiplayer.player.joined.renamed",
                    tfPlayer.func_145748_c_(),
                    s);
            } else {
                chatcomponenttranslation = new ChatComponentTranslation(
                    "multiplayer.player.joined",
                    tfPlayer.func_145748_c_());
            }

            chatcomponenttranslation.getChatStyle()
                .setColor(EnumChatFormatting.YELLOW);
            scm.sendChatMsg(chatcomponenttranslation);
            scm.playerLoggedIn(tfPlayer);
            scm.updateTimeAndWeatherForPlayer(tfPlayer, worldserver);

            tfPlayer.addSelfToInternalCraftingInventory();

            FMLCommonHandler.instance()
                .firePlayerLoggedIn(tfPlayer);
        }
    }

    /**
     * Set destinations for all fake players and begin navigating.
     */
    public void stage3() {
        for (TrueFakePlayer tfPlayer : fakePlayers) {
            tfPlayer.begin();
        }
    }
}
