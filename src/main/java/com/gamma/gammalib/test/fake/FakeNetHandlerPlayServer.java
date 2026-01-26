package com.gamma.gammalib.test.fake;

import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;

public class FakeNetHandlerPlayServer extends NetHandlerPlayServer {

    public FakeNetHandlerPlayServer(MinecraftServer server, TrueFakePlayer player) {
        super(server, new NetworkManager(false), player);
    }

    @Override
    public void onNetworkTick() {}

    @Override
    public void kickPlayerFromServer(String reason) {
        ((TrueFakePlayer) playerEntity).kickPlayerFromServer(reason);
    }

    @Override
    public void processInput(C0CPacketInput packetIn) {}

    @Override
    public void processPlayer(C03PacketPlayer packetIn) {}

    @Override
    public void setPlayerLocation(double x, double y, double z, float yaw, float pitch) {}

    @Override
    public void processPlayerDigging(C07PacketPlayerDigging packetIn) {}

    @Override
    public void processPlayerBlockPlacement(C08PacketPlayerBlockPlacement packetIn) {}

    @Override
    public void onDisconnect(IChatComponent reason) {
        ((TrueFakePlayer) playerEntity).onDisconnect(reason);
    }

    @Override
    public void sendPacket(Packet packetIn) {}

    @Override
    public void processHeldItemChange(C09PacketHeldItemChange packetIn) {}

    @Override
    public void processChatMessage(C01PacketChatMessage packetIn) {}

    @Override
    public void processAnimation(C0APacketAnimation packetIn) {}

    @Override
    public void processEntityAction(C0BPacketEntityAction packetIn) {}

    @Override
    public void processUseEntity(C02PacketUseEntity packetIn) {}

    @Override
    public void processClientStatus(C16PacketClientStatus packetIn) {}

    @Override
    public void processCloseWindow(C0DPacketCloseWindow packetIn) {}

    @Override
    public void processClickWindow(C0EPacketClickWindow packetIn) {}

    @Override
    public void processEnchantItem(C11PacketEnchantItem packetIn) {}

    @Override
    public void processCreativeInventoryAction(C10PacketCreativeInventoryAction packetIn) {}

    @Override
    public void processConfirmTransaction(C0FPacketConfirmTransaction packetIn) {}

    @Override
    public void processUpdateSign(C12PacketUpdateSign packetIn) {}

    @Override
    public void processKeepAlive(C00PacketKeepAlive packetIn) {}

    @Override
    public void processPlayerAbilities(C13PacketPlayerAbilities packetIn) {}

    @Override
    public void processTabComplete(C14PacketTabComplete packetIn) {}

    @Override
    public void processClientSettings(C15PacketClientSettings packetIn) {}

    @Override
    public void processVanilla250Packet(C17PacketCustomPayload packetIn) {}

    @Override
    public void onConnectionStateTransition(EnumConnectionState oldState, EnumConnectionState newState) {}
}
