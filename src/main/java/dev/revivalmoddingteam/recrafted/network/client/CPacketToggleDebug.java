package dev.revivalmoddingteam.recrafted.network.client;

import dev.revivalmoddingteam.recrafted.client.render.RecraftedDebug;
import dev.revivalmoddingteam.recrafted.network.NetworkPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CPacketToggleDebug implements NetworkPacket<CPacketToggleDebug> {

    @Override
    public void encode(CPacketToggleDebug packet, PacketBuffer buf) {

    }

    @Override
    public CPacketToggleDebug decode(PacketBuffer buf) {
        return new CPacketToggleDebug();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handle(CPacketToggleDebug packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(RecraftedDebug::toggleDebugMode);
        ctx.get().setPacketHandled(true);
    }
}
