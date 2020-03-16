package dev.revivalmoddingteam.recrafted.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public interface NetworkPacket<T> {

    void encode(T packet, PacketBuffer buf);

    T decode(PacketBuffer buf);

    void handle(T packet, Supplier<NetworkEvent.Context> ctx);
}
