package dev.revivalmoddingteam.recrafted.network;

import dev.revivalmoddingteam.recrafted.Recrafted;
import dev.revivalmoddingteam.recrafted.network.client.CPacketForceChunkReload;
import dev.revivalmoddingteam.recrafted.network.client.CPacketSyncPlayerData;
import dev.revivalmoddingteam.recrafted.network.client.CPacketSyncWorldData;
import dev.revivalmoddingteam.recrafted.network.client.CPacketToggleDebug;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkHandler {

    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel networkChannel = NetworkRegistry.ChannelBuilder
            .named(Recrafted.getResource("main"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();

    private static int packetID = -1;

    public static void initialize() {
        register(CPacketForceChunkReload.class, new CPacketForceChunkReload());
        register(CPacketSyncWorldData.class, new CPacketSyncWorldData(null));
        register(CPacketToggleDebug.class, new CPacketToggleDebug());
        register(CPacketSyncPlayerData.class, new CPacketSyncPlayerData());
        Recrafted.log.debug("Registered {} packets", packetID);
    }

    public static void sendToAllClients(NetworkPacket<?> packet, World world) {
        world.getServer().getPlayerList().getPlayers().forEach(p -> sendTo(p, packet));
    }

    public static void sendTo(ServerPlayerEntity playerEntity, NetworkPacket<?> packet) {
        networkChannel.sendTo(packet, playerEntity.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendServerPacket(NetworkPacket<?> packet) {
        networkChannel.sendToServer(packet);
    }

    private static <T extends NetworkPacket<T>> void register(Class<T> tClass, NetworkPacket<T> packetInstance) {
        networkChannel.registerMessage(++packetID, tClass, packetInstance::encode, packetInstance::decode, packetInstance::handle);
    }
}
